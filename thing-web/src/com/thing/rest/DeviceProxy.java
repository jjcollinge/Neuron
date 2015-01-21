package com.thing.rest;

import com.thing.api.components.DeviceProxyInterface;
import com.thing.api.events.DataEventProducer;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;

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
