package com.thing.registration;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.RequestResponseController;
import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.impl.MqttConnector;
import com.thing.connectors.impl.SerialConnector;
import com.thing.sessions.ActivityListener;
import com.thing.sessions.SessionManager;

public class RegistrationManager extends RequestResponseController implements
		MessageEventListener, Service {

	private static final Logger log = Logger
			.getLogger(RegistrationManager.class.getName());

	private final String REGISTRATION_TOPIC = "register";
	private HashMap<String, BaseConnector> connectors;
	private static RegistrationManager instance;

	private RegistrationManager() {

		connectors = new HashMap<String, BaseConnector>();

	}

	public static RegistrationManager getInstance() {

		if (instance == null) {
			instance = new RegistrationManager();
		}
		return instance;

	}

	public void start() {

		log.log(Level.INFO, "Starting service...");

		this.MAX_NUMBER_OF_WORKERS = 5;
		connectors.put("SERIAL", new SerialConnector());
		connectors.put("MQTT", new MqttConnector());

		for (BaseConnector connector : connectors.values()) {
			SessionManager.getInstance().getMonitor()
					.registerConnector(connector);
			connector.connect("localhost", "1883");
			connector.addMessageEventListener(this);
			connector.subscribe(this.REGISTRATION_TOPIC, 2);
		}

	}

	public void stop() {

		log.log(Level.INFO, "Stopping service...");
		for (String protocol : connectors.keySet()) {
			connectors.get(protocol).removeMessageEventListener(this);
			connectors.get(protocol).unsubscribe(this.REGISTRATION_TOPIC);
		}

	}

	public synchronized void handleRequest(Message message) {

		log.log(Level.INFO, "Handling registration request");
		doWork(new RegistrationWorker(message));

	}

	public synchronized void handleResponse(Parcel parcel) {

		log.log(Level.INFO, "Handling registration response");
		connectors.get(parcel.getMessage().getProtocol()).sendMessage(parcel);

	}

	public synchronized BaseConnector getConnector(String protocol) {

		return connectors.get(protocol);

	}

	public void onMessageArrived(MessageEvent event) {

		handleRequest(event.getMessage());

	}
}
