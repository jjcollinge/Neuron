package com.registration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.messaging.Message;
import com.thing.messaging.MessageListener;
import com.thing.messaging.MessagingService;
import com.thing.registration.Controller;
import com.thing.registration.ParameterList;


public class RegistrationService extends Controller implements MessageListener {
	
	private static final Logger log = Logger.getLogger( RegistrationService.class.getName() );
	
	private final String REGISTRATION_TOPIC = "register";
	private MessagingService msgService;
	
	public RegistrationService(int maxNumWorkers) {
		super(maxNumWorkers);
		msgService = MessagingService.getService();
	}
	
	public void start() {
		
		if(msgService == null) {
			log.log(Level.WARNING, "Cannot start before message service is running");
		}
		msgService.subscribe(this.REGISTRATION_TOPIC, 2, this);
		
	}
	
	public void stop() {
		msgService.unsubscribe(this.REGISTRATION_TOPIC, this);
	}
	
	private synchronized void register(Message message) {
		
		log.log(Level.INFO, "New registration");	
		log.log(Level.INFO,  message.toString());
		ParameterList params = new ParameterList();
		params.addParameters(message, message.getClass());
		doWork(new RegistrationWorker(params));
		
	}

	public void onMessageArrived(Message message) {
		register(message);
	}
}
