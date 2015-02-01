package com.neuron.registration;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.Configuration;
import com.neuron.api.components.RequestResponseController;
import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.services.Service;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;

public class RegistrationController extends RequestResponseController implements
		MessageEventListener, Service {

	private static final Logger log = Logger
			.getLogger(RegistrationController.class.getName());

	private String registrationTopic = "register";
	private HashMap<String, Connector> connectors;
	private static RegistrationController instance;
	private DeviceDAO dao;
	
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
	public void setup(Configuration config) {
		
		ConnectorFactory factory = new ConnectorFactoryImpl();
		for(String connectorType : factory.getCatalogue()) {
			Connector connector = factory.getConnector(connectorType);
			connectors.put(connectorType, connector);
		}
		
		AbstractDAOFactory daoFactory = DAOFactoryProducer.getFactory("device");
		dao = daoFactory.getDeviceDAO();
		
		String regTopic = config.getProperty("registration_topic");
		if(regTopic != null) {
			this.registrationTopic = regTopic;
		}
	}

	public void start() {
		setMaximumNumberOfWorkers(10);
		
		for(Connector connector : connectors.values()) {
			connector.addMessageEventListener(this);
			connector.subscribe(registrationTopic, 2);
		}
		log.log(Level.INFO, "Started service: " + this.getClass().getSimpleName());
	}

	public void stop() {
		for (Connector connector : connectors.values()) {
			connector.removeMessageEventListener(this);
			connector.unsubscribe(this.registrationTopic);
		}
		log.log(Level.INFO, "Stopped service: " + this.getClass().getSimpleName());
	}
	//=====================================================================
		
	/**
	 * Handle a new registration request
	 */
	public synchronized void handleRequest(Message message) {
		log.log(Level.INFO, "Handling registration request");
		doWork(new RegistrationWorker(message, dao));
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
