package com.thing.api.components;

public interface DeviceProxyInterface {

	// Device API
	public void setup(int sessionId);
	public void startSensorStreaming(int sensorId);
	public void stopSensorStreaming(int sensorId);
	public void operateActuator(int actuatorId, String option);
	
}
