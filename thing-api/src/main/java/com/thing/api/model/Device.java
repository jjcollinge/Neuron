package com.thing.api.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Name: Device ---------------------------------------------------------------
 * Desc: This class models an in system device
 * 
 * @author jcollinge
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

	private int id;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("model")
	private String model;
	@JsonProperty("uri")
	private String uri;
	@JsonProperty("gps")
	private ArrayList<Float> gps;
	@JsonProperty("sensors")
	protected ArrayList<Sensor> sensors;
	@JsonProperty("actuators")
	protected ArrayList<Actuator> actuators;

	// Setters
	public void setId(int id) {
		this.id = id;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setManufacurer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public void setGps(ArrayList<Float> gps) {
		this.gps = gps;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}

	public void setActuators(ArrayList<Actuator> actuators) {
		this.actuators = actuators;
	}

	// Getters
	public int getId() {
		return this.id;
	}

	public String getUri() {
		return this.uri;
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

	public ArrayList<Sensor> getSensors() {
		return this.sensors;
	}

	public ArrayList<Actuator> getActuators() {
		return this.actuators;
	}
	
	public Sensor getSensor(int index) {
		return this.sensors.get(index);
	}

	public Actuator getActuator(int index) {
		return this.actuators.get(index);
	}
}
