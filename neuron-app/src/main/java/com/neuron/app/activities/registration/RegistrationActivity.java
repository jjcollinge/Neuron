package com.neuron.app.activities.registration;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Activity;

/**
 * Wraps all registration sub services and provides a single controlling class.
 * 
 * @author JC
 * 
 */
public class RegistrationActivity extends Activity {

	public RegistrationActivity(String name) {
		super(name);
		addService("RequestHandler", new RegistrationRequestHandler());
		addService("ResponseHandler", new RegistrationResponder());
	}

	public void setup(Configuration config) {
		RegistrationRequestHandler requestHandler = (RegistrationRequestHandler) getService("RequestHandler");
		requestHandler.setup(config);
		requestHandler
				.addRegistrationListener((RegistrationListener) getService("ResponseHandler"));
	}
}
