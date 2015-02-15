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

	public abstract void setup(int sessionId);
	
	public abstract void configureDevice(int refreshRate);

	public abstract void startSensorStreaming(int sensorId);

	public abstract void stopSensorStreaming(int sensorId);

	public abstract void operateActuator(int actuatorId, String option);

	public abstract void onRequest(Request request);

}
