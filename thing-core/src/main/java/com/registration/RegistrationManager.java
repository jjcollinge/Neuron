package com.registration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Manager;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Message;
import com.thing.messaging.MessagingService;


public class RegistrationManager extends Manager implements MessageEventListener {
	
	private static final Logger log = Logger.getLogger( RegistrationManager.class.getName() );
	
	private final String REGISTRATION_TOPIC = "register";
	private MessagingService msgService;
	
	public RegistrationManager() {
		msgService = MessagingService.getService();
	}
	protected void initialise() {
		this.MAX_NUMBER_OF_WORKERS = 5;
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
		doWork(new RegistrationWorker(message));
		
	}
	public void onMessageArrived(MessageEvent event) {
		register(event.getMessage());
	}
}
