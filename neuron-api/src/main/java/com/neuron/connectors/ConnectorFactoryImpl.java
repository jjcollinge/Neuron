package com.neuron.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.neuron.api.data.ConnectorConfiguration;

public class ConnectorFactoryImpl implements ConnectorFactory {

	private static HashMap<String, ConnectorConfiguration> messengers;
	
	public ConnectorFactoryImpl() {
		if(messengers == null) {
			messengers = new HashMap<String, ConnectorConfiguration>();
		}
	}
	
	/**
	 * Registers a new connector to the factory
	 */
	public void registerConnector(ConnectorConfiguration config) {
		messengers.put(config.getType(), config);
	}
	
	/**
	 * Create and return a new Connector of the desired protocol
	 */
	public Connector getConnector(String protocol) {
	
		Connector connector = new Connector();
		ConnectorConfiguration config = messengers.get(protocol);
		
		if(config == null) return null;

		try {
			connector.setMessenger((Messenger) config.getMessengerClass().newInstance());
			connector.connect(config.getHostname(),  config.getPort());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return connector;
	}

	/**
	 * Return a catalogue of all available connector types
	 */
	public List<String> getCatalogue() {
		
		List<String> types = new ArrayList<String>();
		for(String id : messengers.keySet()) {
			types.add(id);
		}
		return types;
	}

}
