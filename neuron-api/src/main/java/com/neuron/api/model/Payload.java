package com.neuron.api.model;

public class Payload {

	private Object payload;
	
	public Payload(Object data) {
		this.payload = data;
	}
	
	public Object getPayload() {
		return this.payload;
	}
	
}
