package com.neuron.api.events;

/**
 * Listen for sensor data events
 * @author JC
 *
 */
public interface DataEventListener {
	
	public void onDataArrived(DataEvent dataEvent);
		
}
