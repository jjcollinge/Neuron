package com.neuron.app.activities.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.neuron.api.model.Device;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration {

	@JsonProperty("regAddress")
	private String regAddress;
	@JsonProperty("device")
	private Device device;
	private Map<String, List<String>> properties;

	public Registration() {
		properties = new HashMap<String, List<String>>();
	}
	
	// Setters
	public void setRegistrationAddress(String returnAddress) {
		regAddress = returnAddress;
	}
	
	public void setDevice(Device device) {
		this.device = device;
	}
	
	// Getters
	public String getRegistrationAddress() {
		return this.regAddress;
	}

	public Device getDevice() {
		return this.device;
	}
	
	public void addProperty(String key, String...values) {
		ArrayList<String> vals = new ArrayList<String>();
		for(String prop : values) {
			vals.add(prop);
		}
		properties.put(key, vals);
	}
	
	public ArrayList<String> getProperty(String key) {
		return (ArrayList<String>) properties.get(key);
	}

}
