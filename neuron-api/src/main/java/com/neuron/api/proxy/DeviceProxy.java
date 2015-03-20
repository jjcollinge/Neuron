package com.neuron.api.proxy;

import com.neuron.api.events.DataEventProducer;
import com.neuron.api.events.RequestListener;
import com.neuron.api.request.Request;

/**
 * The base proxy brings together a number of interfaces and a base class to
 * provide the functionality required to interact successfully with both the
 * devices and web app.
 * 
 * @author JC
 * 
 */
public abstract class DeviceProxy extends DataEventProducer implements
		DeviceProxyInterface, RequestListener {

	/**
	 * Setup the device proxy with a session id
	 */
	public abstract void setup(int sessionId);
	
	/**
	 * Set any configuration information
	 * @param refreshRate
	 */
	public abstract void configureDevice(int refreshRate);

	/**
	 * Start sensor streaming
	 */
	public abstract void startSensorStreaming(int sensorId);

	/**
	 * Stop sensor streaming
	 */
	public abstract void stopSensorStreaming(int sensorId);

	/**
	 * Operate an actuator
	 */
	public abstract void operateActuator(int actuatorId, String option);

	/**
	 * On request event
	 */
	public abstract void onRequest(Request request);

}
