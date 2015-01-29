package com.neuron.api.connectors;

import java.util.List;

import com.neuron.api.data.ConnectorConfiguration;

/**
 * Factory to produce protocol agnostic connectors.
 * At least one connector configuration must be 
 * registered before any services are started.
 * @author JC
 *
 */
public interface ConnectorFactory {

	/**
	 * Register a new connector type
	 * @param config
	 */
	public void registerConnector(ConnectorConfiguration config);

	/**
	 * Get new connector for given protocol
	 * 
	 * @param protocol
	 * @return
	 */
	public Connector getConnector(String protocol);

	/**
	 * Get a list of all supported protocols
	 * 
	 * @return
	 */
	public List<String> getCatalogue();

}
