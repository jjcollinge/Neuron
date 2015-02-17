package com.neuron.app.activities.sessionisation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.core.SessionDaemon;
import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.data_access.DeviceDAOFactory;
import com.neuron.api.model.Payload;
import com.neuron.api.model.Session;
import com.neuron.api.response.Response;

/**
 * The session daemon is responsible for polling session
 * timestamps and pinging the device if it is at risk
 * of becoming stale. If it does not get a response the
 * daemon will remove the device associated with that
 * particular session.
 * @author JC
 *
 */
public class SessionDaemonImpl implements SessionDaemon {

	private static final Logger log = Logger.getLogger(SessionDaemon.class
			.getName());

	private HashMap<Integer, Session> activeSessions;
	private DeviceDAO deviceDao;
	private volatile boolean running;
	
	private final int SEC = 1000;
	private int timeout = SEC * 20;
	private int polling_period = SEC * 30;

	public SessionDaemonImpl() {
		activeSessions = new HashMap<Integer, Session>();
		deviceDao = new DeviceDAOFactory().getDeviceDAO();
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
		Payload payload = new Payload("PING");
		Response response = new Response(payload);
		response.setStatusCode(200);
		response.addProtocol(session.getContext().getProtocol());
		response.addFormat(session.getContext().getFormat());
		response.addHeader("topic", "devices/" + session.getId() + "/ping/request");
		response.addHeader("qos", "2");

		Adapter adapter = new AdapterFactory().getAdapter(session
				.getContext().getProtocol());
		adapter.send(response);
	}
	
	public void setPingTimeout(int seconds) {
		this.timeout = SEC * seconds;
	}
	
	public void setPingPollingPeriod(int seconds){
		this.polling_period = SEC * seconds;
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

		long pingLimit;
		
		running = true;
		log.log(Level.INFO, "Session deamon now running");

		while (running) {

			HashSet<Integer> pingedDevices = new HashSet<Integer>();

			pingLimit = (System.currentTimeMillis() - timeout) / 1000L;
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
				Thread.sleep(polling_period);
			} catch (InterruptedException e) {
				running = false;
			}

			// Check each device timestamp against the same threshold to see if
			// they've updated. If not then add them to the removal set
			Iterator<Integer> iter = pingedDevices.iterator();
			while(iter.hasNext()) {
				Integer sessionKey = iter.next();
				if (activeSessions.get(sessionKey).isOlder(timeoutLimit)) {
					log.log(Level.WARNING, "Session for device " + sessionKey
							+ " timed out");
					removeSession(sessionKey);
				}
			}
			
		}
	}

}
