package com.neuron.app.activities.sessionisation;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Activity;
import com.neuron.api.events.RequestListener;
import com.neuron.app.activities.registration.RegistrationListener;
import com.neuron.app.activities.registration.RegistrationRequestHandler;

/**
 * Encapsulates the session business logic
 * @author JC
 *
 */
public class SessionActivity extends Activity {
	
	public SessionActivity(String name) {
		super(name);
		addService("SessionHandler", new SessionHandler());
		addService("ActivityListener", new ActivityListener());
	}
	
	public void setup(Configuration config) {
		getService("SessionHandler").setup(config);
		ActivityListener al = (ActivityListener) getService("ActivityListener");
		al.addRequestListener((RequestListener) getService("SessionHandler"));
		RegistrationRequestHandler requestHandler = (RegistrationRequestHandler) getService("RequestHandler");
		if(requestHandler != null) {
			requestHandler.addRegistrationListener((RegistrationListener) getService("SessionHandler"));
		} else {
			System.out.println("ERROR: cannot start service without registration handler");
			stop();
		}
	}

}
