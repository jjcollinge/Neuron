package com.neuron.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds information about a particular connected device.
 * Session are only persistent for the duration of the
 * running system instance and can be removed if the device
 * becomes inactive. Is responsible for creating and
 * maintaining a sessionId for a device.
 * @author JC
 *
 */
public class Session {

	private int id;
	private Long timestamp;
	private Context context;
	private Map<String, List<String>> properties;

	public Session(Integer id) {
		this.id = id;
		timestamp = System.currentTimeMillis() / 1000L;
	}

	public int getId() {
		return this.id;
	}

	public Long getTimestamp() {
		return this.timestamp;
	}

	public void updateTimestamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}

	public boolean isOlder(Long sourceTime) {
		return this.timestamp < sourceTime;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public void addProperty(String key, String...values) {
		if(this.properties == null) {
			properties = new  HashMap<String, List<String>>();
		}
		ArrayList<String> props = new ArrayList<String>();
		for(String vals : values) {
			props.add(vals);
		}
		properties.put(key, props);
	}
	
	public ArrayList<String> getProperty(String key) {
		return (ArrayList<String>) properties.get(key);
	}
}
