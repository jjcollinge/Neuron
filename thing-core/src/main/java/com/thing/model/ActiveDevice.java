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
	public void initialise() {
		for(int i = 0; i < sensors.size(); i++) {
			sensors.get(i).setId(i);
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
	public ArrayList<ActiveSensor> getSensors() {
		return this.sensors;
	}
	public ArrayList<Actuator> getActuator() {
		return this.actuators;
	}
}

