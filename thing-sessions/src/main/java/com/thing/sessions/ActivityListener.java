package com.thing.sessions;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.events.MessageEventProducer;
import com.thing.api.messaging.Message;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;

public class ActivityListener extends MessageEventProducer implements
		MessageEventListener {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());

	private ArrayList<BaseConnector> connectors;

	public ActivityListener() {
		connectors = (ArrayList<BaseConnector>) ConnectorFactory.getInstance()
				.getConnectorList();
		for (BaseConnector connector : connectors) {
			connector.subscribe("/devices/#", 2);
		}
	}

	public void onMessageArrived(MessageEvent event) {

		String json = event.getMessage().getPayload();
		int id = -1;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode obj = mapper.readTree(json);

			JsonNode idObj = obj.get("sessionId");

			id = idObj.asInt();

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
