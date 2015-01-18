package com.thing.sessions;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.model.Session;
import com.thing.connectors.ConnectorFactory;
import com.thing.connectors.impl.ConnectorFactoryImpl;

public class SessionController implements Service, MessageEventListener {

	private static final Logger log = Logger.getLogger(SessionController.class
			.getName());

	private static SessionController instance;

	private ActivityListener monitor;
	
	/*
	 * Daemon thread will ping in-active devices
	 * and filter out stale devices which do not
	 * respond.
	 */
	private Thread daemon;
	private SessionDaemon daemonObject;
	//private ConnectorFactory factory;

	private SessionController() {

		monitor = ActivityListener.getInstance();
		monitor.addMessageEventListener(this);
		daemonObject = new SessionDaemon();
		daemon = new Thread(daemonObject);
		//factory =  new ConnectorFactoryImpl();
	}

	public static SessionController getInstance() {
		if (instance == null) {
			instance = new SessionController();
		}
		return instance;
	}

	/**
	 * The Session Controller is one of the main services provided by
	 * thingMiddleware and thus has to implements the start and stop methods
	 * in order to allow command and control.
	 */
	
	//=====================================================================
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
	//=====================================================================

	/**
	 * Gets the daemon thread to return a Session
	 * @param sessionId
	 * @return
	 */
	public Session getSession(int sessionId) {
		return daemonObject.getSession(sessionId);
	}

	/**
	 * Adds a new Session
	 * @param session
	 */
	public void addSession(Session session) {
		//Connector connector = factory.getConnector((String)session.getProperty("protocol"));
		//connector.subscribe("devices/" + session.getId() + "/ping/response", 2);
		//connector.addMessageEventListener(this);
		daemonObject.addSession(session);
	}

	/**
	 * Remove a current Session
	 * @param sessionId
	 */
	public void removeSession(int sessionId) {
		daemonObject.getSession(sessionId);
	}
	
//	/**
//	 * Attempt to extract a sessionId from
//	 * @param event
//	 * @return
//	 */
//	private int extractSessionId(MessageEvent event) {
//		String json = event.getMessage().getPayload();
//		int id = -1;
//		
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode obj = mapper.readTree(json);
//			
//			JsonNode idObj = obj.get("sessionId");
//	
//			id = idObj.asInt();
//			
//		} catch (Exception e) {
//			log.log(Level.INFO, "Couldn't extract id from message. This message will not update a sessions timestamp");
//		}
//		return id;
//	}

	// This is called by the Activity Listener when a message is exchanged -
	// or when a ping response is fired
	// should be of the format { "data" : "id" }
	public void onMessageArrived(MessageEvent event) {
		
		int sessionId = Integer.valueOf(event.getMessage().getPayload());
		
		if(sessionId >= 0)
			daemonObject.updateTimestamp(sessionId);
	}
}
