package com.thing.sessions;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;

public class SessionManager implements Service, MessageEventListener {

	private static final Logger log = Logger.getLogger(SessionManager.class
			.getName());

	private static SessionManager instance;

	// Monitor which forwards all session message activity to this manager
	private ActivityListener monitor;
	// Daemon thread to remove stale sessions
	private Thread daemon;
	private SessionManagerDaemon daemonObject;

	// Singleton
	private SessionManager() {

		monitor = new ActivityListener();
		monitor.addMessageEventListener(this);
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
		BaseConnector connector = ConnectorFactory.getInstance().getConnector(
				session);
		connector.subscribe("devices/" + session.getId() + "/ping/response", 2);
		connector.addMessageEventListener(this);
		daemonObject.addSession(session);
	}

	public void removeSession(int sessionId) {
		daemonObject.getSession(sessionId);
	}
	
	// Attempts to extract an Id from the message event
	private int extractSessionId(MessageEvent event) {
		String json = event.getMessage().getPayload();
		int id = -1;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode obj = mapper.readTree(json);
			
			JsonNode idObj = obj.get("sessionId");
	
			id = idObj.asInt();
			
		} catch (Exception e) {
			log.log(Level.INFO, "Couldn't extract id from message. This message will not update a sessions timestamp");
		}
		return id;
	}

	// This is called by the Activity Listener when a message is exchanged -
	// or when a ping response is fired
	// should be of the format { "data" : "id" }
	public void onMessageArrived(MessageEvent event) {
		
		int sid = extractSessionId(event);
		
		if(sid >= 0)
			daemonObject.updateTimestamp(sid);
	}
}
