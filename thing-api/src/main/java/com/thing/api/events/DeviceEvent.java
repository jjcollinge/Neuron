package com.thing.api.events;

import java.util.EventObject;

public class DeviceEvent extends EventObject {

	private int deviceId;
	private String command;
	
	public DeviceEvent(Object source, int deviceId, String command) {
		super(source);
		this.deviceId = deviceId;
		this.command = command;
	}
	public int getDeviceId() {
		return this.deviceId;
	}
	public String getCommand() {
		return this.command;
	}

}
