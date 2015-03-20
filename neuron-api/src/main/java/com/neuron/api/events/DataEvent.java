package com.neuron.api.events;

import java.util.EventObject;

/**
 * A data event occurs when data
 * is received from a sensor
 * @author JC
 *
 * @param <T> type of data
 */
public class DataEvent<T> extends EventObject {

	private T data;
	
	/**
	 * Ctor
	 * @param source
	 * @param data
	 */
	public DataEvent(Object source, T data) {
		super(source);
		this.data = data;
	}
	
	/**
	 * Get data
	 * @return
	 */
	public T getData() {
		return this.data;
	}
	
}
