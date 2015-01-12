package com.thing.connectors;

import java.util.HashMap;

import com.thing.api.model.Session;

public class ConnectorFactory {

	private HashMap<String, Class> types; 
	private static ConnectorFactory instance;
	
	private ConnectorFactory() {
		types = new HashMap<String, Class>();
	}
	
	public static ConnectorFactory getInstance() {
		if(instance == null) {
			instance = new ConnectorFactory();
		}
		return instance;
	}
	
	public void registerConnectorType(String type, Class connectorClass) {
		types.put(type, connectorClass);
	}
	
	public Connector getConnector(Session session) {
		
		String protocol = session.getProtocol();
		
		Connector connector;
		for(String type : types.keySet()) {
			if(protocol.equals(type)) {
				try {
					connector = (Connector) types.get(type).newInstance();
					return connector;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
