package com.neuron.app.activities.registration;

/**
 * An interface for classes whom which to listen to registration events
 * @author JC
 *
 */
public interface RegistrationListener {

	/**
	 * Will be called on registration as long as the
	 * class has added itself to a producer
	 * @param registration
	 */
	public void onRegistration(Registration registration);
	
}
