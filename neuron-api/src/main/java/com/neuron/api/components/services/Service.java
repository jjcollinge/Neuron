package com.neuron.api.components.services;

/**
 * Required API for any Service implementations
 * @author JC
 *
 */
public interface Service {

	/**
	 * Initialise anything that needs to be done
	 * before the call to start is made.
	 */
	public abstract void setup();

	/**
	 * Start the service
	 */
	public abstract void start();

	/**
	 * Stop the service
	 */
	public abstract void stop();

}
