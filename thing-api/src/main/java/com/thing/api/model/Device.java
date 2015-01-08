package com.thing.api.model;
import java.util.ArrayList;

import com.google.gson.Gson;


public class Device {

	private String manufacturer;
	private String model;
	private ArrayList<Float> gps;
	protected ArrayList<Sensor> sensors;
	protected ArrayList<Actuator> actuators;
	
	public ArrayList<Sensor> getSensors() {
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

}
