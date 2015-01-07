package com.thing.management;

import com.thing.management.model.Device;


public class EventPayload {

	private String topic;
	private Device device;
	
	public String getTopic() {
		return this.topic;
	}
	public Device getDevice() {
		return this.device;
	}
	
}
