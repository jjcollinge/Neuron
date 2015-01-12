package com.thing.api.events;

import java.util.EventObject;

public class DeviceEvent extends EventObject {

	private int deviceSessionId;
	private String command;
	
	public DeviceEvent(Object source, int sessionId, String command) {
		super(source);
		this.deviceSessionId = sessionId;
		this.command = command;
	}
	public int getDeviceSessionId() {
		return this.deviceSessionId;
	}
	public String getCommand() {
		return this.command;
	}

}
