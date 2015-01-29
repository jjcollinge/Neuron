package com.neuron.api.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Sensor {

	private int id = 0;
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

	// Getters
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
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
