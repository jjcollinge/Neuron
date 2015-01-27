package com.thing.connectors.impl;

import java.util.ArrayList;
import java.util.List;

import com.thing.connectors.Connector;
import com.thing.connectors.ConnectorFactory;

public class ConnectorFactoryImpl implements ConnectorFactory {

	public static String MQTT_SERVER_HOSTNAME;
	public static int MQTT_SERVER_PORT;
	
	/**
	 * Create and return a new Connector of the desired protocol
	 */
	public Connector getConnector(String protocol) {
	
		Connector connector = null;
		if(protocol.equalsIgnoreCase("MQTT")) {
			connector = new Connector();
			connector.setMessenger(new MqttMessenger());
			connector.connect(MQTT_SERVER_HOSTNAME,  MQTT_SERVER_PORT);
		}

		return connector;
	}

	/**
	 * Return a catalogue of all available connector types
	 */
	public List<String> getCatalogue() {
		
		List<String> types = new ArrayList<String>();
		types.add("MQTT");
		return types;
	}

}
