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
public class SessionManager implements Runnable, Service {

	private static final Logger log = Logger.getLogger(SessionManager.class
			.getName());

	private static SessionManager instance;
	// Session storage
	private HashMap<Integer, Session> sessions;
	// Subscribers who get forwarded messages about device additions and
	
	// New devices which have not yet responded to a ping
	private Set<Integer> pendingDevices;
	// Connectors to devices
	private HashMap<String, Connector> connectors;
	
	private ActivityListener monitor;

	private SessionManager() {
		// hidden
		pendingDevices = new HashSet<Integer>();
		connectors = new HashMap<String, Connector>();
		sessions = new HashMap<Integer, Session>();
		monitor = new ActivityListener(this);
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
	}

	private synchronized void forgetSession(int deviceId) {
		log.log(Level.INFO, "Forgetting session");
		sessions.remove(deviceId);
		if (pendingDevices.contains(deviceId))
			pendingDevices.remove(deviceId);
	}

	// Sends a ping to a device. onMessageArrvied will be called in response
	private synchronized void pingDevice(Session session) {
		log.log(Level.INFO, "Pinging device " + session.getDeviceId());
		Parcel parcel = ParcelPacker.makeParcel("PING", session.getFormat(),
				session.getPingAddress() + "/request", session.getProtocol());
		Connector connector = connectors.get(session.getProtocol());
		connector.sendMessage(parcel);
	}
	
	public ActivityListener getMonitor() {
		return this.monitor;
	}

	private synchronized void updateTimestamp(int deviceId) {
		log.log(Level.INFO, "Updating device " + deviceId + "'s session timestamp");
		sessions.get(deviceId).updateTimeStamp();
	}

	public void run() {
		final int MIN = 60000;
		final int SEC = 1000;
		int THRESHOLD = SEC * 30;
		int POLLING_PERIOD = SEC * 60;
		long pingLimit;
		

		while (true) {
			
			HashSet<Integer> pingedDevices = new HashSet<Integer>();
			
			pingLimit = (System.currentTimeMillis() - THRESHOLD) / 1000L;
			long timeoutLimit = (System.currentTimeMillis()) / 1000L;
			
			// Check each device timestamp and ping the ones which exceed the PING_THRESHOLD value
			for (Integer sessionKey : sessions.keySet()) {
				if(sessions.get(sessionKey).before(pingLimit)) {
					pingDevice(sessions.get(sessionKey));
					pingedDevices.add(sessionKey);
				}
			}
			
			// delay for POLLING_PERIOD value; this allows devices time to respond to any pings
			try {Thread.sleep(POLLING_PERIOD);} catch (InterruptedException e) {}
			
			/*
			 * By adding each id from the registry to a set and removing
			 * afterwards you avoid the read / write conflict associated with
			 * removing items during iteration directly.
			 */
			HashSet<Integer> deviceToRemove = new HashSet<Integer>();
			
			// Check each device timestamp against the same threshold to see if they've updated
			for (Integer sessionKey : pingedDevices) {
				if(sessions.get(sessionKey).before(timeoutLimit)) {
					log.log(Level.WARNING,
							"Session for device " + sessionKey
									+ " timed out");
					deviceToRemove.add(sessionKey);
				}
			}
			
			for (Integer key : deviceToRemove) {
				forgetSession(key);
			}
		}
	}
	
	public void onSessionActivity(int deviceId) {
		updateTimestamp(deviceId);
	}
}
