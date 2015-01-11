package com.thing.registration.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.thing.api.model.Device;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration {

	@JsonProperty("returnAddress")
	private String returnAddress;
	@JsonProperty("device")
	private Device device;

	// Setters
	public void setReturnAddress(String returnAddress) {
		this.returnAddress = returnAddress;
	}
	
	public void setDevice(Device device) {
		this.device = device;
	}
	
	// Getters
	public String getReturnAddress() {
		return this.returnAddress;
	}

	public Device getDevice() {
		return this.device;
	}

}
