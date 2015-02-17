package com.neuron.api.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.neuron.api.configuration.ProtocolConfiguration;

/**
 * Provide the implementation details for the dapter
 * Factory.
 * @see com.neuron.api.adapters.AdapterFactory
 * @author JC
 *
 */
public class AdapterFactory {

	private static HashMap<String, ProtocolConfiguration> adapters;

	/**
	 * Initialise the adapters collection if it has not already
	 */
	public AdapterFactory() {
		if (adapters == null) {
			adapters = new HashMap<String, ProtocolConfiguration>();
		}
	}

	/**
	 * Registers a new connector to the factory
	 */
	public void registerAdapter(ProtocolConfiguration config) {
		adapters.put(config.getType(), config);
	}

	/**
	 * Create and return a new Connector of the desired protocol
	 * @return a implementation specific connector
	 */
	public Adapter getAdapter(String protocol) {

		ProtocolConfiguration config = adapters.get(protocol);

		if (config == null)
			return null;

		Adapter adapter = null;
		
		try {
			adapter = (Adapter) config.getMessengerClass().newInstance();
			adapter.connect(config.getHostname(), config.getPort());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return adapter;
	}

	/**
	 * Return a catalogue of all available connector types
	 * @return a list of all connector types
	 */
	public List<String> getCatalogue() {

		List<String> types = new ArrayList<String>();
		for (String id : adapters.keySet()) {
			types.add(id);
		}
		return types;
	}

}
