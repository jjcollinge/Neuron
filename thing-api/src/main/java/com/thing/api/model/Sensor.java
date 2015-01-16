package com.thing.api.model;

import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Name: Sensor 
 * ---------------------------------------------------------------
 * Desc: This class models an in system sensor
 * 
 * @author jcollinge
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Sensor {

	private static final Logger log = Logger.getLogger(Sensor.class.getName());

	private int id = 0;
	private String value;
	@JsonProperty("name")
	private String name;
	@JsonProperty("sense")
	private String sense;
	@JsonProperty("unit")
	private String unit;
	@JsonProperty("type")
	private String type;
	
	public Sensor() {}

	// Setters
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSense(String sense) {
		this.sense = sense;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	// Getters
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public String getSense() {
		return this.sense;
	}

	public String getUnit() {
		return this.unit;
	}

	public String getType() {
		return this.type;
	}
	
}
