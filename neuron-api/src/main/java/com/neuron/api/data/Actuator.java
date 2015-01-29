package com.neuron.api.data;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Actuator {

	private static final Logger log = Logger.getLogger(Actuator.class.getName());
	
	private int id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("options")
	private ArrayList<String> options;
	
	// Setters
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addOption(String option) {
		if(options == null)
			options = new ArrayList<String>();
		this.options.add(option);
	}
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}
	
	// Getters
	public int getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public ArrayList<String> getOptions() {
		return this.options;
	}
	public String getOption(int index) {
		return this.options.get(index);
	}

}
