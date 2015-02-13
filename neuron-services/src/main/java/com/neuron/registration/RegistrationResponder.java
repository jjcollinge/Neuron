package com.neuron.registration;

import java.util.ArrayList;

import com.neuron.api.components.Response;
import com.neuron.api.components.Serializer;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.data.Payload;

/**
 * Responsible for responding to a registration request.
 * @author JC
 *
 */
public class RegistrationResponder implements RegistrationListener {

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
		
		// Serialize the payload into the required format
		String formattedData = Serializer.serialize(format, payload);
		// Construct the response
		Response response = new Response(formattedData);
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

}
