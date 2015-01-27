package com.thing.storage;

import com.thing.api.model.AbstractDAOFactory;
import com.thing.api.model.DeviceDAO;
import com.thing.api.model.SessionDAO;

public class DeviceDAOFactory extends AbstractDAOFactory {

	/**
	 * Returns the requested type of device dao
	 */
	@Override
	public DeviceDAO getDeviceDAO(String type, String dbhost, String dbname) {
		
		DeviceDAO dao = null;
		if(type.equalsIgnoreCase("MONGODB")) {
			dao = new MongoDBDeviceDAO();
			dao.initialise(dbhost, dbname);
		}
		return dao;
	}

	@Override
	public SessionDAO getSessionDAO(String type, String dbhost, String dbname) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
