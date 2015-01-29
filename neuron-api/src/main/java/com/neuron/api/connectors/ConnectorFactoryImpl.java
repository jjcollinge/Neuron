package com.neuron.api.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.neuron.api.data.ConnectorConfiguration;

/**
 * Provide the implementation details for the Connector
 * Factory.
 * @see com.neuron.api.connectors.ConnectorFactory
 * @author JC
 *
 */
public class ConnectorFactoryImpl implements ConnectorFactory {

	private static HashMap<String, ConnectorConfiguration> messengers;

	/**
	 * Initialise the messenger collection if it has not already
	 */
	public ConnectorFactoryImpl() {
		if (messengers == null) {
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
	 * @return a implementation specific connector
	 */
	public Connector getConnector(String protocol) {

		Connector connector = new Connector();
		ConnectorConfiguration config = messengers.get(protocol);

		if (config == null)
			return null;

		try {
			connector.setMessenger((Messenger) config.getMessengerClass()
					.newInstance());
			connector.connect(config.getHostname(), config.getPort());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return connector;
	}

	/**
	 * Return a catalogue of all available connector types
	 * @return a list of all connector types
	 */
	public List<String> getCatalogue() {

		List<String> types = new ArrayList<String>();
		for (String id : messengers.keySet()) {
			types.add(id);
		}
		return types;
	}

}
