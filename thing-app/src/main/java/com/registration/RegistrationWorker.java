package com.registration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thing.management.ManagementService;
import com.thing.management.model.DeviceWrapper;
import com.thing.messaging.Message;
import com.thing.messaging.MessagePayload;
import com.thing.messaging.MessagingService;
import com.thing.registration.ParameterList;
import com.thing.registration.Worker;


public class RegistrationWorker extends Worker {

	private static final Logger log = Logger.getLogger( RegistrationWorker.class.getName() );
	
	private RegistrationValidator validator;
	private Message message;
	
	public RegistrationWorker(ParameterList params) {
		validator = new RegistrationValidator();
		
		try {
			message = params.getParameter(0);
		} catch(IndexOutOfBoundsException e) {
			log.log(Level.SEVERE, "Number of parameter(s) passed does not match expected");
			return;
		} catch(ClassCastException e) {
			log.log(Level.SEVERE, "Type of parameter(s) passed does not match expected");
			return;
		}
	}
	
	private void doWork() {
	
		MessagePayload payload = message.getPayload();
		
		if(payload.getFormat().equals("JSON")) {
			// validate against schema
			
			DeviceWrapper wrapper = null;
			try {
				Gson gson = new Gson();
				log.log(Level.INFO, "PAYLOAD: " + payload.getData());
				wrapper = gson.fromJson(payload.getData(), DeviceWrapper.class);
			} catch(JsonSyntaxException e) {
				log.log(Level.SEVERE, "Could not transform nested payload to device wrapper", e);
				return;
			}
			
			String responseTopic = wrapper.getTopic();
			Message response = new Message();
			response.setId(message.getId());
			MessagePayload pl = new MessagePayload();
			pl.setData(String.valueOf(message.getId()));
			pl.setTopic(responseTopic);
			response.setMessagePayload(pl);
			MessagingService.getService().SendMessage(response);
			log.log(Level.INFO, "Sending registration response");
			
			ManagementService.getService().add(message.getId(), wrapper.getDevice());
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
