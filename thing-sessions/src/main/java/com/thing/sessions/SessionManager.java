package com.thing.sessions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Service;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.impl.MqttConnector;
import com.thing.storage.MongoDBSessionDAO;

public class SessionManager implements Runnable, Service {

	private static final Logger log = Logger.getLogger(SessionManager.class
			.getName());

	private static SessionManager instance;
	// Session storage
	private HashMap<Integer, Session> sessions;
	// New devices which have not yet responded to a ping
	private Set<Integer> pendingDevices;
	// Connectors to devices
	private HashMap<String, BaseConnector> connectors;
	// Monitor which forwards all session message activity to this manager
	private ActivityListener monitor;
	// Persistent storage
	private MongoDBSessionDAO dao;

	private SessionManager() {
		// hidden
		pendingDevices = new HashSet<Integer>();
		connectors = new HashMap<String, BaseConnector>();
		sessions = new HashMap<Integer, Session>();
		monitor = new ActivityListener(this);
		dao = new MongoDBSessionDAO();
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	public void start() {
		log.log(Level.INFO, "Starting service...");
		BaseConnector c = new MqttConnector();
		c.connect("localhost", "1883");
		connectors.put("MQTT", c);

		for (BaseConnector connector : connectors.values()) {
			SessionManager.getInstance().getMonitor()
					.registerConnector(connector);
		}

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
		dao.remove(deviceId);
	}

	// Sends a ping to a device. onMessageArrvied will be called in response
	private synchronized void pingDevice(Session session) {	
		log.log(Level.INFO, "Pinging device " + session.getId());
		Parcel parcel = ParcelPacker.makeParcel("PING", session.getFormat(),
				session.getPingAddress() + "/request", session.getProtocol());
		BaseConnector connector = connectors.get(session.getProtocol());
		connector.sendMessage(parcel);
	}

	public ActivityListener getMonitor() {	
		return this.monitor;
	}
	
	public Session getSession(int deviceId) {
		Session session = sessions.get(deviceId);
		log.log(Level.INFO, "Returning session " + session.getId());
		return session;
	}

	private synchronized void updateTimestamp(int deviceId) {	
		log.log(Level.INFO, "Updating device " + deviceId
				+ "'s session timestamp");
		sessions.get(deviceId).updateTimeStamp();
	}

	public void run() {
		
		final int SEC = 1000;
		int THRESHOLD = SEC * 30;
		int POLLING_PERIOD = SEC * 60;
		long pingLimit;

		while (true) {

			HashSet<Integer> pingedDevices = new HashSet<Integer>();

			pingLimit = (System.currentTimeMillis() - THRESHOLD) / 1000L;
			long timeoutLimit = (System.currentTimeMillis()) / 1000L;

			// Check each device timestamp and ping the ones which exceed the
			// PING_THRESHOLD value
			for (Integer sessionKey : sessions.keySet()) {
				if (sessions.get(sessionKey).before(pingLimit)) {
					pingDevice(sessions.get(sessionKey));
					pingedDevices.add(sessionKey);
				}
			}

			// delay for POLLING_PERIOD value; this allows devices time to
			// respond to any pings
			try {
				Thread.sleep(POLLING_PERIOD);
			} catch (InterruptedException e) {
			}

			/*
			 * By adding each id from the registry to a set and removing
			 * afterwards you avoid the read / write conflict associated with
			 * removing items during iteration directly.
			 */
			HashSet<Integer> deviceToRemove = new HashSet<Integer>();

			// Check each device timestamp against the same threshold to see if
			// they've updated
			for (Integer sessionKey : pingedDevices) {
				if (sessions.get(sessionKey).before(timeoutLimit)) {
					log.log(Level.WARNING, "Session for device " + sessionKey
							+ " timed out");
					deviceToRemove.add(sessionKey);
				}
			}

			for (Integer key : deviceToRemove) {
				forgetSession(key);
			}
		}
	}

	// This is called by the Activity Listener when a message is exchanged
	public void onSessionActivity(int deviceId) {
		updateTimestamp(deviceId);
	}
}
