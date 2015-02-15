package com.neuron.api.identification;

/**
 * Simple id generator which return a new
 * Integer value per call to its generateId()
 * method.
 * @author JC
 *
 */
public class IdGenerator {

	private static int counter = 0;

	/**
	 * Generates a unique to this instance of
	 * IdGenerator id.
	 * @return int id
	 */
	public static int generateId() {
		return counter++;
	}

}
