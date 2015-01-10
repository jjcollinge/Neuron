package com.thing.sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.thing.api.components.Service;
import com.thing.api.events.DeviceEvent;
import com.thing.api.events.DeviceEventListener;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.connectors.Connector;
import com.thing.connectors.MqttConnector;
import com.thing.sessions.model.Ping;
import com.thing.sessions.model.Session;

/**
 * Name: SessionManager
 * --------------------------------------------------------------- 
 * Desc: The SessionManager is responsible for keeping on active devices in the system.
 * 
 * @author jcollinge
 *
 */
public class SessionManager implements Runnable, MessageEventListener, Service {

	private static final Logger log = Logger.getLogger(SessionManager.class
			.getName());

	private static SessionManager instance;
	// Session storage
	private HashMap<Integer, Session> sessions;
	// Subscribers who get forwarded messages about device additions and
	// removals
	private ArrayList<DeviceEventListener> subscribers;
	// New devices which have not yet responded to a ping
	private Set<Integer> pendingDevices;
	// Connectors to devices
	private HashMap<String, Connector> connectors;

	private SessionManager() {
		// hidden
		subscribers = new ArrayList<DeviceEventListener>();
		pendingDevices = new HashSet<Integer>();
		connectors = new HashMap<String, Connector>();
		sessions = new HashMap<Integer, Session>();
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	public void start() {
		log.log(Level.INFO, "Starting service...");
		Connector c = new MqttConnector();
		c.connect("localhost", "1883");
		c.addMessageEventListener(this);
		connectors.put("MQTT", c);
		new Thread(this).start();
	}

	public void stop() {
		log.log(Level.INFO, "Stopping service...");
	}

	public synchronized void trackDevice(int deviceId, String protocol,
			String format) {
		log.log(Level.INFO, "Tracking a new device");

		Session session = new Session(deviceId, protocol, format);
		sessions.put(deviceId, session);
		connectors.get(session.getProtocol()).subscribe(
				session.getPingAddress() + "/response", 2);
		pendingDevices.add(deviceId);
		this.notifyListeners(deviceId, "ADD");
	}

	private synchronized void forgetSession(int deviceId) {
		log.log(Level.INFO, "Forgetting session");
		sessions.remove(deviceId);
		if (pendingDevices.contains(deviceId))
			pendingDevices.remove(deviceId);
		this.notifyListeners(deviceId, "SUB");
	}

	public synchronized void addDeviceListener(DeviceEventListener listener) {
		this.subscribers.add(listener);
	}

	public synchronized void removeDeviceListener(DeviceEventListener listener) {
		this.subscribers.remove(listener);
	}

	// Sends a ping to a device. onMessageArrvied will be called in response
	private synchronized void pingDevice(Session session) {
		log.log(Level.INFO, "Pinging device " + session.getDeviceId());
		Parcel parcel = ParcelPacker.makeParcel("PING", session.getFormat(),
				session.getPingAddress() + "/request", session.getProtocol());
		Connector connector = connectors.get(session.getProtocol());
		connector.sendMessage(parcel);
	}

	private synchronized void updateTimestamp(int deviceId) {
		sessions.get(deviceId).updateTimeStamp();
	}

	public void run() {
		final int MIN = 60000;
		final int SEC = 1000;
		int TIMEOUT = SEC * 10;
		int POLLING_PERIOD = SEC * 15;
		long threshold;

		while (true) {
			// Check each device timestamps every x seconds
			threshold = (System.currentTimeMillis() - TIMEOUT) / 1000L;

			/*
			 * By adding each id from the registry to a set and removing
			 * afterwards you avoid the read / write conflict associated with
			 * removing items during iteration directly.
			 */
			HashSet<Integer> set = new HashSet<Integer>();

			for (Integer sessionKey : sessions.keySet()) {
				pingDevice(sessions.get(sessionKey));
			}

			// delay for response
			try {
				Thread.sleep(POLLING_PERIOD);
			} catch (InterruptedException e) {
			}

			for (Integer sessionKey : sessions.keySet()) {
				Session session = sessions.get(sessionKey);
				if (!session.after(threshold)) {
					log.log(Level.WARNING,
							"Session for device " + session.getDeviceId()
									+ " timed out");
					set.add(sessionKey);
					notifyListeners(session.getDeviceId(), "SUB");
				}
			}
			for (Integer key : set) {
				forgetSession(key);
			}
		}
	}

	public void notifyListeners(int deviceId, String operation) {
		DeviceEvent event = new DeviceEvent(this, deviceId, operation);
		for (DeviceEventListener subscriber : this.subscribers) {
			subscriber.onDevicesChanged(event);
		}
	}

	public void onMessageArrived(MessageEvent event) {
		ObjectMapper mapper = new ObjectMapper();
		Ping ping = null;
		try {
			ping = mapper
					.readValue(event.getMessage().getPayload(), Ping.class);
		} catch (Exception e) {
			log.log(Level.INFO, "Corrupted ping response received");
		}
		if (ping != null) {
			int deviceId = ping.getId();
			updateTimestamp(deviceId);
			if (pendingDevices.contains(deviceId)) {
				pendingDevices.remove(deviceId);
			}
			log.log(Level.INFO, "Updated timestamp for device " + deviceId);
		}
	}
}
