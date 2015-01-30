package com.neuron.sessions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.data.Session;

public class SessionDaemon implements Runnable {

	private static final Logger log = Logger.getLogger(SessionDaemon.class
			.getName());

	private HashMap<Integer, Session> activeSessions;
	private DeviceDAO deviceDao;
	private volatile boolean running;

	public SessionDaemon() {
		activeSessions = new HashMap<Integer, Session>();
	}

	public void setDeviceDAO(DeviceDAO dao) {
		deviceDao = dao;
	}
	
	/**
	 * Add a session to database and in memory data structure
	 * @param session The session to add
	 */
	public void addSession(Session session) {
		log.log(Level.INFO, "Adding new session " + session.getId());
		activeSessions.put(session.getId(), session);
	}

	/**
	 * Remove session from database and in memory data structure associated
	 * remove device
	 * @param sessionId The session identifier to remove
	 */
	public void removeSession(Integer sessionId) {
		log.log(Level.INFO, "Removing session " + sessionId);
		activeSessions.remove(sessionId);
		deviceDao.remove(sessionId);
	}

	/**
	 * Return a Session from the in memory data structure
	 * @param sessionId The desired session identifier
	 * @return Session The session matching the given session id
	 */
	public Session getSession(Integer sessionId) {
		return activeSessions.get(sessionId);
	}

	/**
	 * Update the timestamp of the session in memory data structure
	 * @param sessionId The session identifier to perform the update on
	 */
	public void updateTimestamp(Integer sessionId) {
		log.log(Level.INFO, "Updating timestamp for session " + sessionId);
		activeSessions.get(sessionId).updateTimestamp();
	}

	/**
	 * Send a ping message to the device to provoke a response
	 * @param session The session to ping
	 */
	private synchronized void pingDevice(Session session) {
		log.log(Level.INFO, "Pinging device " + session.getId());
		Parcel parcel = new Parcel.ParcelBuilder(new Message("PING", 
					session.getContext().getFormat(),
					session.getContext().getProtocol())
				)
				.topic("devices/" + session.getId() + "/ping/request").qos(2)
				.build();
		Connector connector = new ConnectorFactoryImpl().getConnector(session
				.getContext().getProtocol());
		connector.send(parcel);
	}
	
	/**
	 * Stops the running thread
	 */
	public void stop() {
		running = false;
		log.log(Level.INFO, "Session deamon now stopping");
	}

	/**
	 * Separate thread for checking device status based on their timestamp
	 */
	public void run() {

		final int SEC = 1000;
		int THRESHOLD = SEC * 30;
		int POLLING_PERIOD = SEC * 60;
		long pingLimit;
		
		running = true;
		log.log(Level.INFO, "Session deamon now running");

		while (running) {

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
				running = false;
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
