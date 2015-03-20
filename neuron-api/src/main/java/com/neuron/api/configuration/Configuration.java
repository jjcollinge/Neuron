package com.neuron.api.configuration;

import java.util.HashMap;

/**
 * A generic configuration file which wraps
 * a map.
 * CAUTION: Only supports key value pairs
 * @author JC
 *
 */
public class Configuration {

	private HashMap<String, String> properties;
	
	public Configuration() {
		properties = new HashMap<String, String>();
	}
	
	/**
	 * Add a property
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	/**
	 * Get a property
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	
}
