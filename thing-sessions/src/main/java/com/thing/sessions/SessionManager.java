package com.thing.sessions;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Service;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;
import com.thing.storage.MongoDBSessionDAO;

public class SessionManager implements Service {

	private static final Logger log = Logger.getLogger(SessionManager.class
			.getName());

	private static SessionManager instance;


	// Monitor which forwards all session message activity to this manager
	private ActivityListener monitor;
	// Daemon thread to remove stale sessions
	private Thread daemon;
	private SessionManagerDaemon daemonObject;
	// Persistent storage
	private MongoDBSessionDAO dao;

	// Singleton
	private SessionManager() {

		monitor = new ActivityListener(this);
		dao = new MongoDBSessionDAO();
		daemonObject = new SessionManagerDaemon();
		daemon = new Thread(daemonObject);
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	public void start() {
		log.log(Level.INFO, "Starting service...");
		daemon.start();
	}

	public void stop() {
		log.log(Level.INFO, "Stopping service...");
		try {
			daemon.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ActivityListener getMonitor() {
		return this.monitor;
	}

	public Session getSession(int sessionId) {
		return daemonObject.getSession(sessionId);
	}
	
	public void addSession(Session session) {
		BaseConnector connector = ConnectorFactory.getInstance().getConnector(session);
		connector.subscribe("devices/"+session.getId()+"/ping/response", 2);
		monitor.registerConnector(connector);
		daemonObject.addSession(session);
	}
	
	public void removeSession(int sessionId) {
		daemonObject.getSession(sessionId);
	}
	
	// This is called by the Activity Listener when a message is exchanged
	public void onSessionActivity(int sessionId) {
		daemonObject.updateTimestamp(sessionId);
	}
}
