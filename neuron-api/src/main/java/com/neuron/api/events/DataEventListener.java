package com.neuron.api.events;

/**
 * Listen for sensor data events
 * @author JC
 *
 */
public interface DataEventListener {
	
	/**
	 * Event handler on data arrived
	 * @param dataEvent
	 */
	public void onDataArrived(DataEvent dataEvent);
		
}
