package com.neuron.api.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Provides a POJO representation of an actuator.
 * Will be constructed by deserialization.
 * NOTE: Currently only supports JSON deserialization.
 * @author JC
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actuator {

	private int id;
	@JsonProperty("desc")
	private String desc;
	@JsonProperty("options")
	private ArrayList<String> options;
	@JsonProperty("tags")
	protected HashMap<String, String> tags;

	public Actuator() {
		this.options = new ArrayList<String>();
		this.tags = new HashMap<String, String>();
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

	public ArrayList<String> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	public HashMap<String, String> getTags() {
		return tags;
	}

	public void setTags(HashMap<String, String> tags) {
		this.tags = tags;
	}

	public int getId() {
		return id;
	}

	public void addOption(String option) {
		this.options.add(option);
	}
	
}
