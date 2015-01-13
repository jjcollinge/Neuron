package com.thing.api.model;

import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("sense")
	private String sense;
	@JsonProperty("unit")
	private String unit;
	@JsonProperty("type")
	private String type;

	// Setters
	public void setId(int id) {
		this.id = id;
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
