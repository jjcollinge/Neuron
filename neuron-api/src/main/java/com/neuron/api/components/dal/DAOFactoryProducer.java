package com.neuron.api.components.dal;


public class DAOFactoryProducer {

	public static AbstractDAOFactory getFactory(String type) {
		
		if(type.equalsIgnoreCase("device")) {
			return new DeviceDAOFactory();
		} else {
			return null;
		}
	}
}
