package com.neuron.app.activities.registration;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.core.Service;
import com.neuron.api.model.Payload;
import com.neuron.api.response.Response;
import com.neuron.api.serialization.Serializer;

/**
 * Responsible for responding to a registration request.
 * @author JC
 *
 */
public class RegistrationResponder implements RegistrationListener, Service {

	private static final Logger log = Logger.getLogger(RegistrationResponder.class
			.getName());
	
	public RegistrationResponder() {
	}
	
	/**
	 * Send the response to ALL desired brokers
	 * @param response
	 */
	private void sendResponse(Response response) {
		
		ConnectorFactory factory = new ConnectorFactory();
		ArrayList<String> protocols = response.getProtocols();

		for(String protocol : protocols) {
			Connector connector = factory.getConnector(protocol);
			connector.send(response);
			connector.disconnect();
		}
	}

	/**
	 * Convert the registration into a response object
	 * @param registration
	 */
	public void handleResponse(Registration registration) {

		log.info("Handling registration response");
		
		String address = registration.getRegistrationAddress();
		String status = registration.getProperty("status").get(0);
		String format = registration.getProperty("format").get(0);
		String protocol = registration.getProperty("protocol").get(0);
		
		Payload payload = new Payload();
		
		if(status.equalsIgnoreCase("200")) {
			// Successful registration
			payload.setPayload(registration.getProperty("id").get(0));
		} else if(status.equalsIgnoreCase("400")) {
			// registration failed
			payload.setPayload("BAD registration attempt");
		} else if(status.equalsIgnoreCase("500")) {
			// Service failed
			payload.setPayload("the service failed and could not register you");
		}
	
		// Construct the response
		Response response = new Response(payload);
		response.setStatusCode(Integer.valueOf(status));
		response.addFormat(format);
		response.addProtocol(protocol);
		response.addHeader("topic", address);
		
		sendResponse(response);
	}

	/**
	 * Invoked on new registration event
	 */
	public void onRegistration(Registration registration) {
		handleResponse(registration);
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

}
