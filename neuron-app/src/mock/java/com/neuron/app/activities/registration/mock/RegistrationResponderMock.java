package com.neuron.app.activities.registration.mock;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Service;
import com.neuron.api.model.Payload;
import com.neuron.api.response.Response;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationListener;

public class RegistrationResponderMock implements RegistrationListener, Service {

	private Registration lastRegistration;
	private Response lastResponse;
	
	public void setup(Configuration config) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Convert the registration into a response object
	 * @param registration
	 */
	public void handleResponse(Registration registration) {

		String status = registration.getProperty("status").get(0);
		
		String address = null;
		String format = null;
		String protocol = null;
		
		Payload payload = new Payload();
		
		if(status.equalsIgnoreCase("200")) {
			// Successful registration
			address = registration.getRegistrationAddress();
			format = registration.getProperty("format").get(0);
			protocol = registration.getProperty("protocol").get(0);
			payload.setPayload(registration.getProperty("id").get(0));
		} else if(status.equalsIgnoreCase("400")) {
			// registration failed
			payload.setPayload("BAD registration attempt");
		} else if(status.equalsIgnoreCase("500")) {
			// Service failed
			payload.setPayload("the service failed and could not register you");
		}
	
		if(address == null || format == null || protocol == null) {
			this.lastResponse = null;
			this.lastRegistration = null;
			return;
		}
		
		// Construct the response
		Response response = new Response(payload);
		response.setStatusCode(Integer.valueOf(status));
		response.addFormat(format);
		response.addProtocol(protocol);
		response.addHeader("topic", address);
		
		this.lastResponse = response;
		
	}
	
	public Response getLastResponse() {
		return this.lastResponse;
	}
	
	public Registration getLastRegistration() {
		return this.lastRegistration;
	}

	public void onRegistration(Registration registration) {
		lastRegistration = registration;
		handleResponse(registration);
	}

}
