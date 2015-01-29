package com.neuron.api.components.dal;

import java.util.HashMap;

import com.neuron.api.data.DatabaseConfiguration;

public class DeviceDAOFactory extends AbstractDAOFactory {

	private static HashMap<String, DatabaseConfiguration> registeredDAOs; 
	
	public DeviceDAOFactory() {
		if(registeredDAOs == null) {
			registeredDAOs = new HashMap<String, DatabaseConfiguration>();
		}
	}
	
	@Override
	public void registerDAO(DatabaseConfiguration config) {
		registeredDAOs.put(config.getType(), config);
	}
	
	/**
	 * Returns the requested type of device dao
	 */
	@Override
	public DeviceDAO getDeviceDAO(String type) {
		
		DeviceDAO dao = null;
		
		DatabaseConfiguration config = registeredDAOs.get(type);
		Class client = config.getClientClass();
		
		if(client != null) {
			try {
				dao = (DeviceDAO) client.newInstance();
				dao.initialise(config.getHostname(), config.getPort(), config.getDatabaseName());
				return dao;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
