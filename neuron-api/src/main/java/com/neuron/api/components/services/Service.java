package com.neuron.api.components.services;

import com.neuron.api.components.Configuration;

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
	public abstract void setup(Configuration config);

	/**
	 * Start the service
	 */
	public abstract void start();

	/**
	 * Stop the service
	 */
	public abstract void stop();

}
