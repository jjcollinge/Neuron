package com.neuron.api.components.dal;

import com.neuron.api.data.DatabaseConfiguration;

public abstract class AbstractDAOFactory {

	public abstract void registerDAO(DatabaseConfiguration config);
	public abstract DeviceDAO getDeviceDAO(String type);
	
}
