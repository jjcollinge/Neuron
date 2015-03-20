package com.neuron.api.events;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Produces or forwards sensor data events
 * @author JC
 *
 */
public class DataEventProducer {
	
	protected CopyOnWriteArrayList<DataEventListener> listeners;

	public DataEventProducer() {
		listeners = new CopyOnWriteArrayList<DataEventListener>();
	}

	/**
	 * Add an event listener for data events
	 * @param l
	 */
	public void addDataEventListener(DataEventListener l) {
		this.listeners.add(l);
	}

	/**
	 * Remove an event listener for data event
	 * @param l
	 */
	public void removeDataEventListener(DataEventListener l) {
		this.listeners.remove(l);
	}

	/**
	 * Notify event listeners of new data event
	 * @param dataEvent
	 */
	protected void notifyListeners(DataEvent dataEvent) {

		for (DataEventListener listener : listeners) {
			listener.onDataArrived(dataEvent);
		}
	}
	
}
