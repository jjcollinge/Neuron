package com.neuron.registration;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.RequestResponseController;
import com.neuron.api.components.services.Service;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.data.ServiceConfiguration;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;

public class RegistrationController extends RequestResponseController implements
		MessageEventListener, Service {

	private static final Logger log = Logger
			.getLogger(RegistrationController.class.getName());

	private final String REGISTRATION_TOPIC = "register";
	private HashMap<String, Connector> connectors;
	private static RegistrationController instance;
	private String databaseType;
	
	private RegistrationController() {
		connectors = new HashMap<String, Connector>();
	}
	
	public static RegistrationController getInstance() {
		if(instance == null) {
			instance = new RegistrationController();
		}
		return instance;
	}

	/**
	 * The Registration Controller is one of the main services provided by
	 * neuron and thus has to implements the start and stop methods
	 * in order to allow command and control.
	 */
	
	//=====================================================================
	public void setup(ServiceConfiguration config) {
		
		ConnectorFactory factory = new ConnectorFactoryImpl();
		for(String connectorType : config.getConnectorTypes()) {
			Connector connector = factory.getConnector(connectorType);
			connectors.put(connectorType, connector);
		}
		
		databaseType = config.getDatabaseType();
	}

	public void start() {
		log.log(Level.INFO, "Starting service...");

		setMaximumNumberOfWorkers(10);
		
		for(Connector connector : connectors.values()) {
			connector.addMessageEventListener(this);
			connector.subscribe(REGISTRATION_TOPIC, 2);
		}
	}

	public void stop() {
		log.log(Level.INFO, "Stopping service...");
		
		for (Connector connector : connectors.values()) {
			connector.removeMessageEventListener(this);
			connector.unsubscribe(this.REGISTRATION_TOPIC);
		}
	}
	//=====================================================================
		
	/**
	 * Handle a new registration request
	 */
	public synchronized void handleRequest(Message message) {
		log.log(Level.INFO, "Handling registration request");
		doWork(new RegistrationWorker(message, databaseType));
	}

	/**
	 * Handle a registration response
	 */
	public synchronized void handleResponse(Parcel parcel) {
		log.log(Level.INFO, "Handling registration response");
		Connector connector = connectors.get(parcel.getMessage().getProtocol());
		if(connector != null) {
			connector.send(parcel);
		} else {
			log.log(Level.INFO, "Could not respond to registration, no conenctor for type: " + parcel.getMessage().getProtocol());
		}
	}

	/**
	 * New message arrived from connectors
	 */
	public void onMessageArrived(MessageEvent event) {
		handleRequest(event.getMessage());
	}
}
