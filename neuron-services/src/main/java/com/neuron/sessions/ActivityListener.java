package com.neuron.sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.Deserializer;
import com.neuron.api.components.Request;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.events.RequestEventProducer;
import com.neuron.api.events.RequestListener;

public class ActivityListener extends RequestEventProducer implements
		RequestListener {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());

	private static ActivityListener instance;
	private ArrayList<Connector> connectors;

	/**
	 * Create a connector for all protocols, they will already be connected
	 */
	private ActivityListener() {
		connectors = new ArrayList<Connector>();

		ConnectorFactory factory = new ConnectorFactory();
		ArrayList<String> types = (ArrayList<String>) factory.getCatalogue();
		for (String type : types) {
			Connector connector = factory.getConnector(type);
			connector.addRequestListener(this);
			connector.subscribe("devices/#", 2);
			connectors.add(connector);
		}
	}

	/**
	 * Singleton, only one activity listener is required per instane of the
	 * middleware
	 * 
	 * @return ActivityListener The listener
	 */
	public static ActivityListener getInstance() {
		if (instance == null) {
			instance = new ActivityListener();
		}
		return instance;
	}

	/**
	 * Called when a new message is received from one of the connectors
	 */
	public void onRequest(Request request) {

		// extract the payload
		String payload = (String) request.getData();
		String format = (String) request.getFormat();

		
		
//		String sid = (String) map.get((Object)"sessionId");
//		
//		if(sid != null) {
//			Request req = new Request();
//			req.setData("UPDATE");
//			req.addHeader("id", sid);
//			notifyListeners(req);
//		} else {
//			log.log(Level.INFO, "None update request");
//		}
	}
}
