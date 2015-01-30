package com.neuron.sessions;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.services.Service;
import com.neuron.api.data.Session;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;

public class SessionController implements Service, MessageEventListener {

	private static final Logger log = Logger.getLogger(SessionController.class
			.getName());

	private static SessionController instance;

	private ActivityListener monitor;

	/*
	 * Daemon thread will ping in-active devices and filter out stale devices
	 * which do not respond.
	 */
	private Thread daemon;
	private SessionDaemon daemonObject;

	private SessionController() {

		monitor = ActivityListener.getInstance();
		monitor.addMessageEventListener(this);
		daemonObject = new SessionDaemon();
		daemon = new Thread(daemonObject);
		// factory = new ConnectorFactoryImpl();
	}

	public static SessionController getInstance() {
		if (instance == null) {
			instance = new SessionController();
		}
		return instance;
	}

	/**
	 * The Session Controller is one of the main services provided by
	 * thingMiddleware and thus has to implements the start and stop methods in
	 * order to allow command and control.
	 */

	// =====================================================================
	public void setup() {

		AbstractDAOFactory deviceDAOFactory = DAOFactoryProducer
				.getFactory("device");
		DeviceDAO dao = deviceDAOFactory.getDeviceDAO();
		daemonObject.setDeviceDAO(dao);

	}

	public void start() {
		daemon.start();
		log.log(Level.INFO, "Started service: " + this.getClass().getSimpleName());
	}

	public void stop() {	
		try {
			daemonObject.stop();
			daemon.interrupt();
			daemon.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.log(Level.INFO, "Stopped service: " + this.getClass().getSimpleName());
	}

	// =====================================================================

	/**
	 * Gets the daemon thread to return a Session
	 * @param sessionId The session id
	 * @return Session The session matching the given session id
	 */
	public Session getSession(int sessionId) {
		return daemonObject.getSession(sessionId);
	}

	/**
	 * Adds a new Session
	 * @param session The session to add
	 */
	public void addSession(Session session) {
		// Connector connector =
		// factory.getConnector((String)session.getProperty("protocol"));
		// connector.subscribe("devices/" + session.getId() + "/ping/response",
		// 2);
		// connector.addMessageEventListener(this);
		daemonObject.addSession(session);
	}

	/**
	 * Remove a current Session
	 * @param sessionId The session identified to remove
	 */
	public void removeSession(int sessionId) {
		daemonObject.getSession(sessionId);
	}

	// This is called by the Activity Listener when a message is exchanged -
	// or when a ping response is fired
	// should be of the format { "data" : "id" }
	public void onMessageArrived(MessageEvent event) {

		int sessionId = Integer.valueOf(event.getMessage().getPayload());

		if (sessionId >= 0)
			daemonObject.updateTimestamp(sessionId);
	}
}
