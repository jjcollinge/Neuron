package com.neuron.api.core;

/**
 * Daemon used to perform a looped tasks
 * @author JC
 *
 */
public interface Daemon extends Runnable {

	/**
	 * Add any additional functionality needed
	 * to control a deamon
	 */
	public void stop();
	
}
