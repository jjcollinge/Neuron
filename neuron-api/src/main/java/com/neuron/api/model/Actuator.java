package com.neuron.api.model;

import java.util.ArrayList;

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
	@JsonProperty("name")
	private String name;
	@JsonProperty("options")
	private ArrayList<String> options;

	// Setters
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Set name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Add actuator option (equivalent to controls)
	 * @param option
	 */
	public void addOption(String option) {
		if (options == null)
			options = new ArrayList<String>();
		this.options.add(option);
	}

	/**
	 * Set options as array
	 * @param options
	 */
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	/**
	 * Get actuator id
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Get name
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get all options
	 * @return
	 */
	public ArrayList<String> getOptions() {
		return this.options;
	}

	/**
	 * Get option by index
	 * @param index
	 * @return
	 */
	public String getOption(int index) {
		return this.options.get(index);
	}

}
