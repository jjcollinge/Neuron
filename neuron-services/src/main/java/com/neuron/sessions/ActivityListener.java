package com.neuron.sessions;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.neuron.api.components.Configuration;
import com.neuron.api.components.Request;
import com.neuron.api.components.Service;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.events.RequestEventProducer;
import com.neuron.api.events.RequestListener;

public class ActivityListener extends RequestEventProducer implements
		RequestListener, Service {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());

	private ArrayList<Connector> connectors;

	/**
	 * Create a connector for all protocols, they will already be connected
	 */
	public ActivityListener() {
		connectors = new ArrayList<Connector>();
	}

	/**
	 * Called when a new message is received from one of the connectors
	 */
	public void onRequest(Request request) {

		String topic = request.getHeader("topic").get(0);
		
		if(topic.contains("/response")) {
			String parts[] = topic.split("/");
			String sid = parts[1];
			
			Request req = new Request();
			req.setData("UPDATE");
			req.addHeader("id", sid);
			notifyListeners(req);
		}
	}

	public void setup(Configuration config) {
		// TODO Auto-generated method stub
		
	}

	public void start() {
		ConnectorFactory factory = new ConnectorFactory();
		ArrayList<String> types = (ArrayList<String>) factory.getCatalogue();
		for (String type : types) {
			Connector connector = factory.getConnector(type);
			connector.addRequestListener(this);
			connector.subscribe("devices/#", 2);
			connectors.add(connector);
		}
	}

	public void stop() {
		for(Connector connector : connectors) {
			connector.unsubscribe("devices/#");
			connector.removeRequestListener(this);
		}
	}
}
