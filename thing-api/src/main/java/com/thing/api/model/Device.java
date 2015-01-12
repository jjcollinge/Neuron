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

	private int sessionId;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("model")
	private String model;
	@JsonProperty("uri")
	private String uri;
	@JsonProperty("geo")
	private GeoPoint geo;
	@JsonProperty("sensors")
	protected ArrayList<Sensor> sensors;
	@JsonProperty("actuators")
	protected ArrayList<Actuator> actuators;

	// Setters
	public void setSessionId(int id) {
		this.sessionId = id;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setManufacurer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public void setModel(String model) {
		this.model = model;
	}

	public void setGeo(GeoPoint geo) {
		this.geo = geo;
	}
	
	public void setGeo(double lon, double lat) {
		this.geo = new GeoPoint(lon, lat);
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}

	public void setActuators(ArrayList<Actuator> actuators) {
		this.actuators = actuators;
	}
	
	public void addSensor(Sensor sensor) {
		if(sensors == null)
			sensors = new ArrayList<Sensor>();
		sensors.add(sensor);			
	}
	
	public void addActuator(Actuator actuator) {
		if(actuators == null)
			actuators = new ArrayList<Actuator>();
		actuators.add(actuator);
		
	}

	// Getters
	public int getSessionId() {
		return this.sessionId;
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

	public GeoPoint getGeo() {
		return this.geo;
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
