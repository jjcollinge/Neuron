package com.thing.model;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Sensor;
import com.thing.messaging.MessagingService;

public class ActiveSensor<T> extends Sensor implements MessageEventListener {

	private T currentValue;
	
	public ActiveSensor() {
	}
	public T getValue() {
		return this.currentValue;
	}
	
	public void updateValue(int deviceId) {
		MessagingService ms = MessagingService.getInstance();
		ms.subscribe("device/"+deviceId+"/job/response", 2, this);
		ms.sendMessage(ParcelPacker.makeParcel(deviceId, "SENSOR:"+getId()+":GET", "JSON", "device/"+deviceId+"/job/request"));
	}

	public void onMessageArrived(MessageEvent event) {
		String valueString = event.getMessage().getPayload();
		this.currentValue = (T) valueString;
	}
	
}
