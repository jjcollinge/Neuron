package com.neuron.api.components;

/**
 * Represents the API available to external
 * components wishing to interact with a 
 * particular device. This is separate to a
 * {@see com.neuron.api.data.Device } as this
 * does not represent the devices data and is
 * only an interface from wish to perform
 * actions on said device.
 * @author JC
 *
 */
public interface DeviceProxyInterface {

	//TODO: Finish JAVADOC
	
	public void setup(int sessionId);
	public void startSensorStreaming(int sensorId);
	public void stopSensorStreaming(int sensorId);
	public void operateActuator(int actuatorId, String option);
	
}
