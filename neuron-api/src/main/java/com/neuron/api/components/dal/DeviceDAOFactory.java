package com.neuron.api.components.dal;

import com.neuron.api.data.DatabaseConfiguration;

/**
 * Provides a data access object from a given
 * database configuration. A dao class MUST 
 * be registered before any services are started.
 * There is only one type of dao permitted in any
 * running instance of the system. However, that
 * dao implementation can be different per 
 * system instance.
 * @author JC
 *
 */
public class DeviceDAOFactory extends AbstractDAOFactory {

	private static DeviceDAO DAO; 
	
	public DeviceDAOFactory() {
	}
	
	/**
	 * Register the ONLY dao type for this system instance.
	 * This method will attempt to create and initialise the 
	 * DAO, if it fails to do so it will throw an exception
	 */
	@Override
	public void registerDAO(DatabaseConfiguration config) {
		@SuppressWarnings("rawtypes")
		Class client = config.getClientClass();
		if(client != null) {
			try {
				DAO = (DeviceDAO) client.newInstance();
				DAO.initialise(config.getHostname(), config.getPort(),  config.getDatabaseName());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the ONLY device dao in this running system
	 */
	@Override
	public DeviceDAO getDeviceDAO() {
		return DAO;
	}
	
}
