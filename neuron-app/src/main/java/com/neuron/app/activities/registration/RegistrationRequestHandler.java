package com.neuron.app.activities.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Service;
import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.data_access.DeviceDAOFactory;
import com.neuron.api.identification.IdGenerator;
import com.neuron.api.request.Request;
import com.neuron.api.request.RequestHandler;

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
	private HashMap<String, Adapter> adapters;
	private String regTopic = "register";
	private RegistrationDeserializer deserializer;
	private DeviceDAO deviceDao;
	
	public RegistrationRequestHandler() {
		adapters = new HashMap<String, Adapter>();
		listeners = new ArrayList<RegistrationListener>();
		deserializer = new RegistrationDeserializer();
		deviceDao = new DeviceDAOFactory().getDeviceDAO();
	}

	/**
	 * Any setup that is required before this service is started should be done
	 * here
	 */
	public void setup(Configuration config) {
		String topic = config.getProperty("registration_topic");

		if (topic != null)
			regTopic = topic;

		AdapterFactory factory = new AdapterFactory();
		for (String protocol : factory.getCatalogue()) {
			Adapter adapter = factory.getAdapter(protocol);
			adapters.put(protocol, adapter);
			adapter.addRequestListener(this);
			adapter.subscribe(regTopic, 2);
		}
		deserializer.addFormat("json", new JsonRegistrationMapper());
	}

	/**
	 * Start processing registration requests
	 */
	public void start() {
		if(!this.processingThread.isAlive()) {
			this.processingThread.start();
		}
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
		for (Adapter adapter : adapters.values()) {
			adapter.removeRequestListener(this);
			adapter.unsubscribe(regTopic);
		}
	}

	public void setDeviceDao(DeviceDAO dao) {
		this.deviceDao = dao;
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
		
		if (registration != null && registration.isOk()) {
			
			// store the device
			deviceDao.insert(registration.getDevice());

			registration.addProperty("protocol", request.getProtocol());
			registration.addProperty("status", "200");
			registration.addProperty("id",
					String.valueOf(IdGenerator.generateId()));
			
			log.log(Level.INFO, "Successful registration");

		} // else handle errors... 
		else {
			registration = new Registration();
			registration.addProperty("status", "400");
		}
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
