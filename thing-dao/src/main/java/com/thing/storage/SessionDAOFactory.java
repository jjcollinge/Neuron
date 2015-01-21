package com.thing.storage;

import com.thing.api.model.AbstractDAOFactory;
import com.thing.api.model.DeviceDAO;
import com.thing.api.model.SessionDAO;

public class SessionDAOFactory extends AbstractDAOFactory {

	@Override
	public DeviceDAO getDeviceDAO(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionDAO getSessionDAO(String type) {
		
		SessionDAO dao = null;
		if(type.equalsIgnoreCase("MONGODB")) {
			dao = new MongoDBSessionDAO();
		}
		return dao;
	}

}
