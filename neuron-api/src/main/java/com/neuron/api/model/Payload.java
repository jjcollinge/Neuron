package com.neuron.api.model;

/**
 * Holds data in a standard format
 * as a Transfer Object
 * @author JC
 *
 */
public class Payload {

	private Object payload;
	
	public Payload(Object data) {
		this.payload = data;
	}
	
	public Object getPayload() {
		return this.payload;
	}
	
}
