package com.thing.api.components;

public interface DeviceProxy {

	// Device API
	public void startSensorStreaming(int sensorId);
	public void stopSensorStreaming(int sensorId);
	public void operateActuator(int actuatorId, String option);
	
}
