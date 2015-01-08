package com.thing.model;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.thing.api.events.MessageEventListener;
import com.thing.api.model.Actuator;

public class ActiveDevice {
	
	private String manufacturer;
	private String model;
	private ArrayList<Float> gps;
	protected ArrayList<ActiveSensor> sensors;
	protected ArrayList<Actuator> actuators;
	
	public ActiveDevice() {
	}
	public void activate(int deviceId) {
		for(ActiveSensor sensor : sensors) {
			sensor.updateValue(deviceId);
		}
	}
	public String getManufacturer() {
		return this.manufacturer;
	}
	public String getModel() {
		return this.model;
	}
	public ArrayList<Float> getGps() {
		return this.gps;
	}
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	public String getSensorValue(int sensorId) {
		return sensors.get(sensorId).getValue();
	}
	public void listenToSensor(int sensorId, MessageEventListener listener) {
		sensors.get(sensorId).addSensorEventListener(listener);
	}
	public void bindToSensor(int sensorId, int deviceId, MessageEventListener listener) {
		sensors.get(sensorId).bind(deviceId, listener);
	}
	
}

