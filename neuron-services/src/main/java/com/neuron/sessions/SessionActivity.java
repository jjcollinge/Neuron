package com.neuron.sessions;

import com.neuron.api.components.Configuration;
import com.neuron.api.components.services.Activity;
import com.neuron.api.events.RequestListener;
import com.neuron.registration.RegistrationListener;
import com.neuron.registration.RegistrationRequestHandler;

/**
 * Wraps all session management sub services and provides
 * a single controlling class.
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
