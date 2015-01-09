package com.thing.management;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.thing.api.components.Service;
import com.thing.api.events.DeviceEvent;
import com.thing.api.events.DeviceEventListener;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.messaging.Connector;
import com.thing.messaging.MqttConnector;
import com.thing.messaging.SerialConnector;
import com.thing.model.ActiveDevice;
import com.thing.sessions.IdGenerator;
import com.thing.sessions.Session;

public class SessionManager implements Runnable, MessageEventListener, Service {

	private static final Logger log = Logger.getLogger( SessionManager.class.getName() );
	
	// Stores messanger id's with device descriptors
	private HashMap<Integer, Session> sessions;
	private ArrayList<DeviceEventListener> subscribers;
	private static SessionManager instance;
	private Set<Integer> pendingDevices;
	private HashMap<String, Connector> connectors;
	
	private SessionManager() {
		// hidden
		sessions = new HashMap<Integer, Session>();
		subscribers = new ArrayList<DeviceEventListener>();
		pendingDevices = new HashSet<Integer>();
		connectors = new HashMap<String, Connector>();
	}
	public static SessionManager getInstance() {
		if(instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	public void start() {
		log.log(Level.INFO, "Starting service...");
		connectors.put("SERIAL", new SerialConnector());
		connectors.put("MQTT", new MqttConnector());
		for(String protocol : connectors.keySet()) {
			connectors.get(protocol).connect("localhost", "1883");
			connectors.get(protocol).addMessageEventListener(this);
		}
		new Thread(this).start();
	}
	public void stop() {
		log.log(Level.INFO, "Stopping service...");
	}
	public synchronized void add(Session session) {
		log.log(Level.INFO, "Adding an device to registry");
		sessions.put(session.getDeviceId(), session);
		String topic = "devices/"+session.getDeviceId()+"/ping/response";
		Connector connector = connectors.get(session.getProtocol());
		connector.addMessageEventListener(this);
		connector.subscribe(topic, 2);
		pendingDevices.add(session.getDeviceId());
		this.notifyListeners(session.getDeviceId(), "ADD");
	}
	public synchronized void remove(int sessionId) {
		log.log(Level.INFO, "Removing an adapter to registry");
		sessions.remove(sessionId);
		pendingDevices.remove(sessionId);
		this.notifyListeners(sessionId, "SUB");
	}
	public synchronized HashMap<Integer, Session> getDevices() {
		return this.sessions;
	}
	public void addDeviceListener(DeviceEventListener listener) {
		this.subscribers.add(listener);
	}
	public void removeDeviceListener(DeviceEventListener listener) {
		this.subscribers.remove(listener);
	}
	private void pingDevice(int id) {
		String message = "PING";
		String topic = "devices/"+id+"/ping/request";
		Session session = sessions.get(id);
		session.lockOnSend();
		Parcel parcel = ParcelPacker.makeParcel(message, session.getFormat(), topic, session.getProtocol());
		connectors.get(session.getProtocol()).sendMessage(parcel, session);
	}
	public HashMap<String, Connector> getConnectors() {
		return this.connectors;
	}
	public void run() {
		
		final int MIN = 60000;
		final int SEC = 1000;
		int TIMEOUT = SEC * 10;
		int POLLING_PERIOD = SEC * 15;
		long threshold;
		
		while(true) {
			
			// Check each device timestamps every x seconds
			threshold = (System.currentTimeMillis() - TIMEOUT) / 1000L;
			
			/* By adding each id from the registry to a set and removing afterwards
			 * you avoid the read / write conflict associated with removing items
			 * during iteration directly.
			 */
			HashSet<Integer> set = new HashSet<Integer>();			
			
			for(Integer id : sessions.keySet()) {
				pingDevice(id);
			}
			
			// delay for response
			try {Thread.sleep(POLLING_PERIOD);} catch (InterruptedException e) {}
			
			for(Integer id : sessions.keySet()) {
				if(!sessions.get(id).after(threshold)) {
					log.log(Level.WARNING, "Device " + id + " timed out");
					set.add(id);
					notifyListeners(id, "SUB");
				}
			}
			for(Integer i : set) {
				sessions.remove(i);
			}
		}
	}
	public void notifyListeners(int deviceId, String operation) {
		DeviceEvent event = new DeviceEvent(this, deviceId, operation);
		for(DeviceEventListener subscriber : this.subscribers) {
			subscriber.onDevicesChanged(event);
		}
	}
	public void onMessageArrived(MessageEvent event) {
		String payload = event.getMessage().getPayload();
		Gson gson = new Gson();
		Ping ping = gson.fromJson(payload, Ping.class);
		int deviceId = ping.getId();
		Session session = sessions.get(deviceId);
		if(session != null) {
			session.unlock();
			session.updateTimeStamp();
			if(pendingDevices.contains(deviceId)) {
				//ActiveDevice device = session.getDevice();
				//device.activate(session.getSessionId());
				pendingDevices.remove(deviceId);
			}
			log.log(Level.INFO, "Updated timestamp for device " + deviceId);
		}
	}
}
