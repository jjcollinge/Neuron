package com.thing.model;

import java.util.ArrayList;
import java.util.LinkedList;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Sensor;
import com.thing.messaging.MessagingService;

public class ActiveSensor extends Sensor implements MessageEventListener {

	private LinkedList<String> values;
	private String previousValue;
	private ArrayList<MessageEventListener> listeners;
	private String bind = "";
	
	public ActiveSensor() {
		values = new LinkedList<String>();
		listeners = new ArrayList<MessageEventListener>();
	}
	public String getValue() {
		// if there are values
		if(!values.isEmpty()) {
			// update previous value
			previousValue = values.getFirst();
		}
		// return value
		return previousValue;
	}
	public void updateValue(int deviceId) {
		MessagingService ms = MessagingService.getInstance();
		String topic = "device/"+deviceId+"/sensor/"+this.getId();
		ms.subscribe(topic + "/response", 2, this);
		ms.sendMessage(ParcelPacker.makeParcel(deviceId, "GET"+bind, "JSON", topic));
	}
	public void bind(int deviceId, MessageEventListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
		bind = "_BIND";
		updateValue(deviceId);
	}
	public void addSensorEventListener(MessageEventListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	public void removeSensorEventListener(MessageEventListener listener) {
		listeners.remove(listener);
	}
	public void onMessageArrived(MessageEvent event) {
		String valueString = event.getMessage().getPayload();
		this.values.addLast(valueString);
		for(MessageEventListener listener : listeners){
			listener.onMessageArrived(event);
		}
	}
}
