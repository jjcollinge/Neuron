package com.neuron.api.core;

import com.neuron.api.configuration.Configuration;

/**
 * A service encapsulates a particular business
 * service and and provides a control interface.
 * @author JC
 *
 */
public interface Service {

	/**
	 * Initialise anything that needs to be done
	 * before the call to start is made.
	 * @param Configuration service configuration
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
