package com.thing.registration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thing.api.components.Worker;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.DeviceWrapper;
import com.thing.management.DeviceManager;
import com.thing.messaging.MessagingService;
import com.thing.messaging.SerialMessageWrapper;


public class RegistrationWorker extends Worker {

	private static final Logger log = Logger.getLogger( RegistrationWorker.class.getName() );
	
	private RegistrationValidator validator;
	private Message message;
	
	public RegistrationWorker(Message message) {
		this.message = message;
		validator = new RegistrationValidator();
	}
	
	protected void doWork() {
		
		int id = message.getId();
		String msg = message.getPayload();
		String format = message.getFormat();
		
		if(format.equals("JSON")) {
			
			// validate against schema
			if(!validator.isValid(msg)) return;
			
			Registration registration = null;
			try {
				Gson gson = new Gson();
				registration = gson.fromJson(msg, Registration.class);
			} catch(JsonSyntaxException e1) {
				log.log(Level.FINEST, "Couldn't extract registration");
				return;
			}
			
			String returnTopic = registration.getReturnAddress();
			
			Message response = new Message(id, String.valueOf(id), "JSON");
			Parcel parcel = ParcelPacker.makeParcel(response, returnTopic);
			
			MessagingService.getInstance().sendMessage(parcel);
			log.log(Level.INFO, "Sending registration response");
			
			DeviceManager.getInstance().add(message.getId(), registration.getDevice());
			log.log(Level.INFO, "Stored adapter in registry");
			// Store device too
			
		} // else other supported formats (XML)
		
	}

	public void run() {
		try {
			doWork();
		} finally {
			finishWork();
		}
	}
}
