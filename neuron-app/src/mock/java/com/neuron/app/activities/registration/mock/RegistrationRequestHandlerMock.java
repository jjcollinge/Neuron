package com.neuron.app.activities.registration.mock;

import java.util.ArrayList;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Service;
import com.neuron.api.request.Request;
import com.neuron.api.request.RequestHandler;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationListener;

public class RegistrationRequestHandlerMock extends RequestHandler implements
Service {

	private ArrayList<RegistrationListener> listeners;
	
	public RegistrationRequestHandlerMock() {
		listeners = new ArrayList<RegistrationListener>();
	}
	
	public void setup(Configuration config) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRequest(Request request) {
		
		Registration registration = (Registration) request.getData();
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
