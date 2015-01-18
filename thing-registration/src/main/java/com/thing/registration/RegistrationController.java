package com.thing.registration;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.RequestResponseController;
import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.connectors.Connector;
import com.thing.connectors.ConnectorFactory;
import com.thing.connectors.impl.ConnectorFactoryImpl;

public class RegistrationController extends RequestResponseController implements
		MessageEventListener, Service {

	private static final Logger log = Logger
			.getLogger(RegistrationController.class.getName());

	private final String REGISTRATION_TOPIC = "register";
	private HashMap<String, Connector> connectors;
	private static RegistrationController instance;

	// Singleton, each instance of the middleware only requires on instance of this service
	
	private RegistrationController() {
		connectors = new HashMap<String, Connector>();
	}

	public static RegistrationController getInstance() {
		if (instance == null) {
			instance = new RegistrationController();
		}
		return instance;
	}

	/**
	 * The Registration Controller is one of the main services provided by
	 * thingMiddleware and thus has to implements the start and stop methods
	 * in order to allow command and control.
	 */
	
	//=====================================================================
	public void start() {
		log.log(Level.INFO, "Starting service...");

		setMaximumNumberOfWorkers(10);
		
		ConnectorFactory factory = new ConnectorFactoryImpl();
		Connector connector = factory.getConnector("MQTT");
		connector.connect("localhost", 1883);
		connector.addMessageEventListener(this);
		connector.subscribe(this.REGISTRATION_TOPIC,  2);	
		connectors.put("MQTT", connector);
	}

	public void stop() {
		log.log(Level.INFO, "Stopping service...");
		for (String protocol : connectors.keySet()) {
			connectors.get(protocol).removeMessageEventListener(this);
			connectors.get(protocol).unsubscribe(this.REGISTRATION_TOPIC);
		}
	}
	//=====================================================================
		
	/**
	 * Handle a new registration request
	 */
	public synchronized void handleRequest(Message message) {
		log.log(Level.INFO, "Handling registration request");
		doWork(new RegistrationWorker(message));
	}

	/**
	 * Handle a registration response
	 */
	public synchronized void handleResponse(Parcel parcel) {
		log.log(Level.INFO, "Handling registration response");
		connectors.get(parcel.getMessage().getProtocol()).send(parcel);
	}

	/**
	 * New message arrived from connectors
	 */
	public void onMessageArrived(MessageEvent event) {
		handleRequest(event.getMessage());
	}
}
