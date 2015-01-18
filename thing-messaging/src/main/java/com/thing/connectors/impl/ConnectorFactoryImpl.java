package com.thing.connectors.impl;

import java.util.ArrayList;
import java.util.List;

import com.thing.connectors.Connector;
import com.thing.connectors.ConnectorFactory;

public class ConnectorFactoryImpl implements ConnectorFactory {

	/**
	 * Create and return a new Connector of the desired protocol
	 */
	public Connector getConnector(String protocol) {
	
		Connector connector = null;
		if(protocol.equals("MQTT")) {
			connector = new Connector();
			connector.setMessenger(new MqttMessenger());
			connector.connect("localhost",  1883);
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
