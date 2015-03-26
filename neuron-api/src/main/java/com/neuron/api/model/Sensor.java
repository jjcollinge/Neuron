package com.neuron.api.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Provides a POJO representation of a sensor.
 * Will be constructed by deserialization.
 * NOTE: Currently only supports JSON deserialization.
 * @author JC
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Sensor {

	private int id = 0;
	@JsonProperty("desc")
	private String desc;
	@JsonProperty("sense")
	private String sense;
	@JsonProperty("unit")
	private String unit;
	@JsonProperty("tags")
	protected HashMap<String, String> tags;
	
	public Sensor() {
		this.tags = new HashMap<String, String>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public HashMap<String, String> getTags() {
		return tags;
	}
	
	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}

	
}
