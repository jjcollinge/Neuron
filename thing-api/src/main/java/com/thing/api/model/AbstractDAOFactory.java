package com.thing.api.model;

public abstract class AbstractDAOFactory {

	public abstract DeviceDAO getDeviceDAO(String type);
	public abstract SessionDAO getSessionDAO(String type);
	
}
