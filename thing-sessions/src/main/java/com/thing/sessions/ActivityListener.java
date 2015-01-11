package com.thing.sessions;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.events.MessageEventProducer;

public class ActivityListener implements MessageEventListener {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());
	
	private ArrayList<MessageEventProducer> connectors;
	private SessionManager sessionManager;
	
	public ActivityListener(SessionManager manager) {
		connectors = new ArrayList<MessageEventProducer>();
		sessionManager = manager;
	}
	
	public void registerConnector(MessageEventProducer connector) {
		connectors.add(connector);
		connector.addMessageEventListener(this);
	}
	
	public void unregisterConnector(MessageEventProducer connector) {
		connector.removeMessageEventListener(this);
		connectors.remove(connector);
	}

	public void onMessageArrived(MessageEvent event) {

		String json = event.getMessage().getPayload();
		int id = -1;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode obj = mapper.readTree(json);
			
			JsonNode idObj = obj.get("id");
	
			id = idObj.asInt();
			
			if(id != -1)
				sessionManager.onSessionActivity(id);
			
		} catch (Exception e) {
			log.log(Level.INFO, "Couldn't extract id from message. This message will not update a sessions timestamp");
		}
	}
}
