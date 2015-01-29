package com.neuron.api.components.dal;

/**
 * The DAOFactoryProducer will return a new
 * factory implementation based on a given
 * string. This allows new DAO types for different
 * data to be added at a later date.
 * @author JC
 *
 */
public class DAOFactoryProducer {

	/**
	 * Get an AbstractDAOFactory implementation for storing
	 * a given type of data
	 * @param type of data
	 * @return a DAOFactory to handle the given type of data
	 */
	public static AbstractDAOFactory getFactory(String type) {

		if (type.equalsIgnoreCase("device")) {
			return new DeviceDAOFactory();
		} else {
			return null;
		}
	}
}
