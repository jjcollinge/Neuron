package com.thing.registration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thing.api.components.Worker;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.management.SessionManager;
import com.thing.sessions.Session;


public class RegistrationWorker extends Worker {

	private static final Logger log = Logger.getLogger( RegistrationWorker.class.getName() );
	
	private RegistrationValidator validator;
	private Message message;
	
	public RegistrationWorker(Message message) {
		this.message = message;
		validator = new RegistrationValidator();
	}
	
	protected void doWork() {
		
		String payload = message.getPayload();
		String format = message.getFormat();
		String protocol = message.getProtocol();		
		
		if(format.equals("JSON")) {
			
			// validate against schema
			if(!validator.isValid(payload)) return;
			
			Registration registration = null;
			try {
				Gson gson = new Gson();
				registration = gson.fromJson(payload, Registration.class);
			} catch(JsonSyntaxException e1) {
				log.log(Level.FINEST, "Couldn't extract registration");
				return;
			}
			
			Session session = new Session(registration.getDevice(), protocol, format);

			String returnTopic = registration.getReturnAddress();
			
			Message response = new Message(String.valueOf(session.getDeviceId()), format, protocol);
			Parcel parcel = ParcelPacker.makeParcel(response, returnTopic);
			
			SessionManager.getInstance().add(session);
			log.log(Level.INFO, "Stored adapter in registry");
			
			RegistrationManager.getInstance().getConnector(protocol).sendMessage(parcel, session);
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
