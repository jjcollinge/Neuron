package com.thing.registration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Manager;
import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Message;
import com.thing.messaging.MessagingService;


public class RegistrationManager extends Manager implements MessageEventListener, Service {
	
	private static final Logger log = Logger.getLogger( RegistrationManager.class.getName() );
	
	private final String REGISTRATION_TOPIC = "register";
	private MessagingService msgService;
	private static RegistrationManager instance;
	
	private RegistrationManager() {
		msgService = MessagingService.getInstance();
	}
	public static RegistrationManager getInstance() {
		if(instance == null) {
			instance = new RegistrationManager();
		}
		return instance;
	}
	protected void initialise() {
		this.MAX_NUMBER_OF_WORKERS = 5;
	}
	public void start() {
		log.log(Level.INFO, "Starting service...");
		initialise();
		if(msgService == null) {
			log.log(Level.WARNING, "Cannot start before message service is running");
		}
		msgService.subscribe(this.REGISTRATION_TOPIC, 2, this);
		
	}
	public void stop() {
		log.log(Level.INFO, "Stopping service...");
		msgService.unsubscribe(this.REGISTRATION_TOPIC, this);
	}
	private synchronized void register(Message message) {
		
		log.log(Level.INFO, "New registration");	
		doWork(new RegistrationWorker(message));
		
	}
	public void onMessageArrived(MessageEvent event) {
		register(event.getMessage());
	}
}
