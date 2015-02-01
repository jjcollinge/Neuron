package com.neuron.api.components;

import java.util.HashMap;

public class Configuration {

	private HashMap<String, String> properties;
	
	public Configuration() {
		properties = new HashMap<String, String>();
	}
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
}
