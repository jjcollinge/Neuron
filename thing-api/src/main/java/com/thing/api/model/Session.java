package com.thing.api.model;

import java.util.HashMap;

import com.thing.api.components.IdGenerator;


public class Session {
	
	private HashMap<String, Object> properties;
	private int id;
	private Long timestamp;
	
	public Session() {
		properties = new HashMap<String, Object>();
		id = IdGenerator.generateId();
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
	public void addProperty(String key, Object value) {
		properties.put(key, value);
	}
	public void removeProperty(String key) {
		properties.remove(key);
	}
	public Object getProperty(String key) {
		return properties.get(key);
	}
	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}
	
}
