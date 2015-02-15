package com.neuron.app.activities.sessionisation;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Activity;
import com.neuron.api.core.Service;
import com.neuron.api.events.RequestListener;
import com.neuron.api.model.Context;
import com.neuron.api.model.Session;
import com.neuron.api.request.Request;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationListener;

/**
 * The SessionHandler is one of the main services provided by the Neuron
 * platform and thus has to implements the start and stop methods in order to
 * allow command and control. The purpose of the session controller is to check
 * for session that have become stale and remove them from the device
 * repository.
 * 
 * @author JC
 * 
 */
public class SessionHandler implements Service, RegistrationListener,
		RequestListener {

	private static final Logger log = Logger.getLogger(SessionHandler.class
			.getName());

	/**
	 * Daemon thread will ping in-active devices and filter out stale devices
	 * which do not respond.
	 */
	private Thread daemon;
	private SessionDaemon daemonObject;

	public SessionHandler() {
		daemonObject = new SessionDaemon();
		daemon = new Thread(daemonObject);
	}

	/**
	 * -------------------------------------------------------------------------
	 * Service Layer
	 * -------------------------------------------------------------------------
	 */

	/**
	 * @see com.neuron.api.core.Activity
	 */
	public void setup(Configuration config) {

		String timeout = config.getProperty("ping_timeout");
		String pollPeriod = config.getProperty("ping_polling_period");
		if (timeout != null) {
			daemonObject.setPingTimeout(Integer.valueOf(timeout));
		}
		if (pollPeriod != null) {
			daemonObject.setPingPollingPeriod(Integer.valueOf(pollPeriod));
		}
	}

	/**
	 * @see com.neuron.api.core.Activity
	 */
	public void start() {
		daemon.start();
		log.log(Level.INFO, "Started service: "
				+ this.getClass().getSimpleName());
	}

	/**
	 * @see com.neuron.api.core.Activity
	 */
	public void stop() {
		try {
			daemonObject.stop();
			daemon.interrupt();
			daemon.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.log(Level.INFO, "Stopped service: "
				+ this.getClass().getSimpleName());
	}

	/**
	 * Gets the daemon thread to return a Session
	 * 
	 * @param sessionId
	 *            The session id
	 * @return Session The session matching the given session id
	 */
	public Session getSession(int sessionId) {
		return daemonObject.getSession(sessionId);
	}

	/**
	 * Adds a new Session
	 * 
	 * @param session
	 *            The session to add
	 */
	private void addSession(Session session) {
		daemonObject.addSession(session);
	}

	/**
	 * Remove a current Session
	 * 
	 * @param sessionId
	 *            The session identified to remove
	 */
	private void removeSession(int sessionId) {
		daemonObject.getSession(sessionId);
	}

	/**
	 * This is called once a new registration has been completed
	 */
	public void onRegistration(Registration registration) {
		String protocol = registration.getProperty("protocol").get(0);
		String format = registration.getProperty("format").get(0);
		String id = registration.getProperty("id").get(0);
		String regAddress = registration.getRegistrationAddress();

		Session session = new Session(Integer.valueOf(id));
		session.setContext(new Context(protocol, format));
		session.addProperty("registrationAddress", regAddress);
		addSession(session);
	}

	/**
	 * This is called by the activity listener when a request is sent on any
	 * topic from the active broker
	 */
	public void onRequest(Request request) {
		String sid = request.getHeader("id").get(0);
		int id = Integer.valueOf(sid);
		daemonObject.updateTimestamp(id);
	}

	public void attachDependency(Activity dependency) {

	}

}
