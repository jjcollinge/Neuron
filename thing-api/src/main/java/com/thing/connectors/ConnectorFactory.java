package com.thing.connectors;

import java.util.List;


public interface ConnectorFactory {
	
	public Connector getConnector(String protocol);
	public List<String> getCatalogue();
	
}
