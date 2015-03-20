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

	/**
	 * Ctor
	 * @param id
	 */
	public Session(Integer id) {
		this.id = id;
		timestamp = System.currentTimeMillis() / 1000L;
	}

	/**
	 * Get id
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Get session timestamp
	 * @return
	 */
	public Long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Update session timestamp
	 */
	public void updateTimestamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}

	/**
	 * Boolean check against timestamp
	 * @param sourceTime
	 * @return
	 */
	public boolean isOlder(Long sourceTime) {
		return this.timestamp < sourceTime;
	}
	
	/**
	 * Set session context
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * Get session context
	 * @return
	 */
	public Context getContext() {
		return this.context;
	}
	
	/**
	 * Add a property
	 * @param key
	 * @param values
	 */
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
	
	/**
	 * Get property
	 * @param key
	 * @return
	 */
	public ArrayList<String> getProperty(String key) {
		return (ArrayList<String>) properties.get(key);
	}
}
