package com.neuron.api.data;

import java.util.Map;

public class Request {

	private Map<String, String[]> data;
	
	public Request(Map<String, String[]> data) {
		this.data = data;
	}
	
	public Request() {}
	
	/**
	 * Add a property to the current data map
	 * @param key
	 * @param values
	 */
	public void addProperty(String key, String... values) {
		if(data != null) {
			data.put(key, values);
		}
	}
	
	/**
	 * Get propertie(s) associated with a given key
	 * @param key
	 * @return
	 */
	public String[] getPropterty(final String key) {
		if(data != null) {
			return data.get(key);
		}
		return null;
	}
	
	/**
	 * Get entire data map
	 * @return
	 */
	public Map<String, String[]> getPropertyMap() {
		return data;
	}
	
}
