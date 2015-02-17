package com.neuron.app.activities.sessionisation;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.configuration.Configuration;
import com.neuron.api.core.Service;
import com.neuron.api.events.RequestEventProducer;
import com.neuron.api.events.RequestListener;
import com.neuron.api.request.Request;

/**
 * Responsible for listening to all communication
 * and notifying the relevant clients. This stops
 * the session controller pinging the devices whilst
 * they are busy talking to other parts of the system.
 * This conserves the limited battery life of devices.
 * @author JC
 *
 */
public class ActivityListener extends RequestEventProducer implements
		RequestListener, Service {

	private static final Logger log = Logger.getLogger(ActivityListener.class
			.getName());

	private ArrayList<Adapter> adapters;

	/**
	 * Create a connector for all protocols, they will already be connected
	 */
	public ActivityListener() {
		adapters = new ArrayList<Adapter>();
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
		AdapterFactory factory = new AdapterFactory();
		ArrayList<String> types = (ArrayList<String>) factory.getCatalogue();
		for (String type : types) {
			Adapter adapter = factory.getAdapter(type);
			adapter.addRequestListener(this);
			adapter.subscribe("devices/#", 2);
			adapters.add(adapter);
		}
	}

	public void stop() {
		for(Adapter adapter : adapters) {
			adapter.unsubscribe("devices/#");
			adapter.removeRequestListener(this);
		}
	}
}
