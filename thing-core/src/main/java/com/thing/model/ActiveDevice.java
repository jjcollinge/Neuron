package com.thing.model;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.thing.api.model.Actuator;
import com.thing.api.model.Device;
import com.thing.api.model.Sensor;

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
	public ArrayList<ActiveSensor> getSensors() {
		return this.sensors;
	}
	public ArrayList<Actuator> getActuator() {
		return this.actuators;
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

	public <T> T getValueOf(int deviceId, int sensorId) {
		if(sensorId > sensors.size() || sensorId < 0) {
			System.out.println("Sensor index out of range");
			return null;
		}
		// Grab sensor
		ActiveSensor sensor = sensors.get(sensorId);
		// Get current value
		T value = (T) sensor.getValue();
		// Update value
		sensor.updateValue(deviceId);
		return value;
	}
	
}

