package com.neuron.api.components.dal;

import com.neuron.api.data.DatabaseConfiguration;

/**
 * Any DAO implementations should be available from this abstract factory. This
 * facilitates future expansion it is implementation agnostic and new methods
 * can be added to allow new DAOs.
 * 
 * @author JC
 * 
 */
public abstract class AbstractDAOFactory {

	/**
	 * Register a particular data access object implementation with the factory
	 * @param config The configuration file of a DAO
	 */
	public abstract void registerDAO(DatabaseConfiguration config);
	
	/**
	 * Returns the ONLY device data access object and is implementation agnostic
	 * @return DeviceDAO The ONLY device data access object
	 */
	public abstract DeviceDAO getDeviceDAO();

}
