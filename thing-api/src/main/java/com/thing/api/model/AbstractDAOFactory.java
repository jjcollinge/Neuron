package com.thing.api.model;

public abstract class AbstractDAOFactory {

	public abstract DeviceDAO getDeviceDAO(String type, String dbhost, String dbname);
	public abstract SessionDAO getSessionDAO(String type, String dbhost, String dbname);
	
}
