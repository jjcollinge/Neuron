package com.neuron.api.core;

import com.neuron.api.configuration.Configuration;

public interface Service {

	/**
	 * Initialise anything that needs to be done
	 * before the call to start is made.
	 */
	public void setup(Configuration config);

	/**
	 * Start the service
	 */
	public void start();

	/**
	 * Stop the service
	 */
	public void stop();
	
}
