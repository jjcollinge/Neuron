package com.neuron.api.components;

import com.neuron.api.events.DataEventProducer;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;

/**
 * The base proxy brings together a number of interfaces and a base class to
 * provide the functionality required to interact successfully with both the
 * devices and web app.
 * 
 * @author JC
 * 
 */
public abstract class DeviceProxy extends DataEventProducer implements
		DeviceProxyInterface, MessageEventListener {

	public abstract void setup(int sessionId);
	
	public abstract void configureDevice(int refreshRate);

	public abstract void startSensorStreaming(int sensorId);

	public abstract void stopSensorStreaming(int sensorId);

	public abstract void operateActuator(int actuatorId, String option);

	public abstract void onMessageArrived(MessageEvent event);

}
