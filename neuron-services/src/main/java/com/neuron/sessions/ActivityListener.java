package com.neuron.sessions;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.Message;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;
import com.neuron.api.events.MessageEventProducer;

public class ActivityListener extends MessageEventProducer implements
		MessageEventListener {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());

	private static ActivityListener instance;
	private ArrayList<Connector> connectors;

	/**
	 * Create a connector for all protocols, they will already be connected
	 */
	private ActivityListener() {
		connectors = new ArrayList<Connector>();
		
		ConnectorFactory factory = new ConnectorFactoryImpl();
		ArrayList<String> types = (ArrayList<String>) factory.getCatalogue();
		for(String type : types) {
			Connector connector = factory.getConnector(type);
			connector.addMessageEventListener(this);
			connector.subscribe("devices/#", 2);
			connectors.add(connector);
		}
	}
	
	/**
	 * Singleton, only one activity listener is required per instane of the middleware
	 * @return
	 */
	public static ActivityListener getInstance() {
		if(instance == null) {
			instance = new ActivityListener();
		}
		return instance;
	}

	/**
	 * Called when a new message is received from one of the connectors
	 */
	public void onMessageArrived(MessageEvent event) {

		// extract the payload
		String json = event.getMessage().getPayload();
		int id = -1;

		/* Attempt to get the sessionId from the payload
		 * and if successful, notify listeners. Otherwise
		 * drop the message.
		 */
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode obj = mapper.readTree(json);
			JsonNode idObj = obj.get("sessionId");
			id = idObj.getIntValue();

			if (id != -1)
				notifyListeners(new MessageEvent(this, new Message(
						String.valueOf(id), event.getMessage().getFormat(),
						event.getMessage().getProtocol())));

		} catch (Exception e) {
			log.log(Level.INFO,
					"Couldn't extract id from message. This message will not update a sessions timestamp");
		}
	}
}
