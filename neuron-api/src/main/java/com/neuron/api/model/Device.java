package com.neuron.api.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Provides a POJO representation of a device.
 * Will be constructed by deserialization.
 * NOTE: Currently only supports JSON deserialization.
 * @author JC
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

	private int sessionId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("geo")
	private GeoPoint geo;
	@JsonProperty("sensors")
	protected ArrayList<Sensor> sensors;
	@JsonProperty("actuators")
	protected ArrayList<Actuator> actuators;
	@JsonProperty("tags")
	protected HashMap<String, String> tags;

	public Device() {
		sensors = new ArrayList<Sensor>();
		actuators = new ArrayList<Actuator>();
	}
	
	public int getSessionId() {
		return sessionId;
	}



	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}



	public String getName() {
		return name;
	}

	

	public void setName(String name) {
		this.name = name;
	}



	public GeoPoint getGeo() {
		return geo;
	}



	public void setGeo(GeoPoint geo) {
		this.geo = geo;
	}

	
	public void setGeo(Double lat, Double lon) {
		this.geo = new GeoPoint(lat, lon);
	}


	public ArrayList<Sensor> getSensors() {
		return sensors;
	}



	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}



	public ArrayList<Actuator> getActuators() {
		return actuators;
	}



	public void setActuators(ArrayList<Actuator> actuators) {
		this.actuators = actuators;
	}



	public HashMap<String, String> getTags() {
		return tags;
	}



	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}
	
	public void addSensor(Sensor sensor) {
		if (sensors == null)
			sensors = new ArrayList<Sensor>();
		sensor.setId(sensors.size());
		sensors.add(sensor);
	}

	public void addActuator(Actuator actuator) {
		if (actuators == null)
			actuators = new ArrayList<Actuator>();
		actuator.setId(actuators.size());
		actuators.add(actuator);

	}
	
	public Actuator getActuator(int index) {
		return this.actuators.get(index);
	}
	
	public Sensor getSensor(int index) {
		return this.sensors.get(index);
	}
}
