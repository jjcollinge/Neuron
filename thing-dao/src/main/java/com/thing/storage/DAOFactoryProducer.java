package com.thing.storage;

import com.thing.api.model.AbstractDAOFactory;

public class DAOFactoryProducer {

	public static AbstractDAOFactory getFactory(String type) {
		
		if(type.equalsIgnoreCase("DEVICE")) {
			return new DeviceDAOFactory();
		} else if(type.equalsIgnoreCase("SESSION")) {
			return new SessionDAOFactory();
		} else {
			return null;
		}
	}
}
