package com.neuron.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.Configuration;
import com.neuron.api.components.IdGenerator;
import com.neuron.api.components.Request;
import com.neuron.api.components.RequestHandler;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.components.services.Service;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;

/**
 * Responsible for defining the logic for handling an incoming registration
 * request.
 * 
 * @author JC
 * 
 */
public class RegistrationRequestHandler extends RequestHandler implements
		Service {

	private static final Logger log = Logger.getLogger(RegistrationRequestHandler.class
			.getName());
	
	private ArrayList<RegistrationListener> listeners;
	private HashMap<String, Connector> connectors;
	private String regTopic = "register";
	private RegistrationDeserializer deserializer;

	public RegistrationRequestHandler() {
		connectors = new HashMap<String, Connector>();
		listeners = new ArrayList<RegistrationListener>();
		deserializer = new RegistrationDeserializer();
	}

	/**
	 * Any setup that is required before this service is started should be done
	 * here
	 */
	public void setup(Configuration config) {
		String topic = config.getProperty("registration_topic");

		if (topic != null)
			regTopic = topic;

		ConnectorFactory factory = new ConnectorFactory();
		for (String connectorType : factory.getCatalogue()) {
			Connector connector = factory.getConnector(connectorType);
			connectors.put(connectorType, connector);
			connector.addRequestListener(this);
			connector.subscribe(regTopic, 2);
		}
		deserializer.addFormat("json", new JsonRegistrationMapper());
	}

	/**
	 * Start processing registration requests
	 */
	public void start() {
		this.processingThread.start();
	}

	/**
	 * Stop processing registration requests
	 */
	public void stop() {
		try {
			this.processingThread.interrupt();
			this.processingThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (Connector connector : connectors.values()) {
			connector.removeRequestListener(this);
			;
			connector.unsubscribe(regTopic);
		}
	}

	/**
	 * Handle a single request
	 */
	@Override
	public void handleRequest(Request request) {

		log.log(Level.INFO, "Handling new request");
		
		// extract context
		String payload = (String) request.getData();

		// attempt to deserialize the registration and set the format
		Registration registration = deserializer.deserialize(payload);
		
		if (registration != null) {
			
			// get a device data access object and store the device
			DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
			dao.insert(registration.getDevice());

			registration.addProperty("protocol", request.getProtocol());
			registration.addProperty("status", "200");
			registration.addProperty("id",
					String.valueOf(IdGenerator.generateId()));
			
			log.log(Level.INFO, "Successful registration");

		} // else handle errors...
		notifyListeners(registration);
	}

	/**
	 * Add a new registration listener
	 * 
	 * @param listener
	 */
	public void addRegistrationListener(RegistrationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a registration listener
	 * 
	 * @param listener
	 */
	public void removeRegistrationListener(RegistrationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify registration listeners
	 * 
	 * @param registration
	 */
	public void notifyListeners(Registration registration) {
		for (RegistrationListener listener : listeners) {
			listener.onRegistration(registration);
		}
	}

}
