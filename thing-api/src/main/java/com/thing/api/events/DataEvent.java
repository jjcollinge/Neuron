package com.thing.api.events;

import java.util.EventObject;

public class DataEvent extends EventObject {

	private String data;
	
	public DataEvent(Object source, String data) {
		super(source);
		this.data = data;
	}
	
	public String getData() {
		return this.data;
	}

}
