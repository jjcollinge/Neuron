package com.neuron.api.connectors;

import java.util.List;

import com.neuron.api.data.ConnectorConfiguration;


public interface ConnectorFactory {
	
	public void registerConnector(ConnectorConfiguration config);
	
	/**
	 * Get new connector for given protocol
	 * @param protocol
	 * @return
	 */
	public Connector getConnector(String protocol);
	/**
	 * Get a list of all supported protocols
	 * @return
	 */
	public List<String> getCatalogue();
	
}
