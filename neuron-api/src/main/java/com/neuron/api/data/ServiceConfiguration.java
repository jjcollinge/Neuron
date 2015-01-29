package com.neuron.api.data;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfiguration {

	List<String> connectorTypes;
	String databaseType;
	
	public ServiceConfiguration() {
		this.connectorTypes = new ArrayList<String>();
	}
	
	public void registerConnectorType(String type) {
		connectorTypes.add(type);
	}
	
	public void registerDatabaseType(String type) {
		databaseType = type;
	}
	
	public List<String> getConnectorTypes() {
		return connectorTypes;
	}
	
	public String getDatabaseType() {
		return databaseType;
	}
}
