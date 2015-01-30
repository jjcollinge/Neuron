package com.neuron.rest;

import com.neuron.api.components.DeviceProxyInterface;
import com.neuron.api.events.DataEventProducer;
import com.neuron.api.events.MessageEvent;
import com.neuron.api.events.MessageEventListener;

/**
 * The base proxy brings together a number of interfaces and
 * a base class to provide the functionality required to 
 * interact successfully with both the devices and web app. 
 * @author JC
 *
 */
public abstract class DeviceProxy extends DataEventProducer implements
		DeviceProxyInterface, MessageEventListener {

	@Override
	public abstract void setup(int sessionId);

	@Override
	public abstract void startSensorStreaming(int sensorId);

	@Override
	public abstract void stopSensorStreaming(int sensorId);

	@Override
	public abstract void operateActuator(int actuatorId, String option);

	@Override
	public abstract void onMessageArrived(MessageEvent event);

}
