package com.thing.sessions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;
import com.thing.storage.MongoDBDeviceDAO;
import com.thing.storage.MongoDBSessionDAO;

public class SessionManagerDaemon implements Runnable {

	private static final Logger log = Logger.getLogger(SessionManagerDaemon.class
			.getName());
	
	private HashMap<Integer, Session> activeSessions;
	private MongoDBSessionDAO sessionDao;
	private MongoDBDeviceDAO deviceDao;
	
	public SessionManagerDaemon() {
		activeSessions = new HashMap<Integer, Session>();
		sessionDao = new MongoDBSessionDAO();
		deviceDao = new MongoDBDeviceDAO();
	}
	
	public void addSession(Session session) {
		log.log(Level.INFO, "Adding new session " + session.getId());
		activeSessions.put(session.getId(), session);
		sessionDao.insert(session);
	}
	
	public void removeSession(Integer sessionId) {
		log.log(Level.INFO, "Removing session " + sessionId);
		activeSessions.remove(sessionId);
		// remove the device from both collections
		// TODO: This should be handled better
		sessionDao.remove(sessionId);
		deviceDao.remove(sessionId);
	}
	
	public Session getSession(Integer sessionId) {
		return activeSessions.get(sessionId);
	}
	
	public void updateTimestamp(Integer sessionId) {
		log.log(Level.INFO, "Updating timestamp for session " + sessionId);
		activeSessions.get(sessionId).updateTimestamp();
	}
	
	// Sends a ping to a device. onMessageArrvied will be called in response
	private synchronized void pingDevice(Session session) {
		log.log(Level.INFO, "Pinging device " + session.getId());
		Parcel parcel = ParcelPacker.makeParcel("PING", (String) session.getProperty("format"),
				"devices/" + session.getId() + "/ping/request", (String) session.getProperty("protocol"));
		BaseConnector connector = ConnectorFactory.getInstance().getConnector(
				session);
		connector.sendMessage(parcel);
	}
	
	// This is called by the Activity Listener when a message is exchanged
	public void onSessionActivity(int sessionId) {
		updateTimestamp(sessionId);
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
			for (Integer sessionKey : activeSessions.keySet()) {
				if (activeSessions.get(sessionKey).isOlder(pingLimit)) {
					pingDevice(activeSessions.get(sessionKey));
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
			// they've updated. If not then add them to the removal set
			for (Integer sessionKey : pingedDevices) {
				if (activeSessions.get(sessionKey).isOlder(timeoutLimit)) {
					log.log(Level.WARNING, "Session for device " + sessionKey
							+ " timed out");
					deviceToRemove.add(sessionKey);
				}
			}
			
			// Remove stale devices
			for (Integer key : deviceToRemove) {
				removeSession(key);
			}
		}
	}
	
}
