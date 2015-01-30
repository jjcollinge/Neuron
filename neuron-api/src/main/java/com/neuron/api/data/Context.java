package com.neuron.api.data;

/**
 * Context is designed to contain all the information
 * about a given situation. Is only concerned with
 * the environment and situational information.
 * Data in the context is READ-ONLY
 * @author JC
 *
 */
public class Context {

	private String protocol;	// protocol to communicate
	private String format;		// format of messages
 
	/**
	 * Holds the information about a current situation.
	 * All data is required and must be provided in the
	 * constructor.
	 * @param protocol The context's protocol
	 * @param format The context's format
	 */
	public Context(String protocol, String format) {
		this.protocol = protocol;
		this.format = format;
	}
	
	/**
	 * Get the protocol from the context
	 * @return String The context's protocol
	 */
	public String getProtocol() {
		return this.protocol;
	}
	
	/**
	 * Get the format from the context
	 * @return String The context's format
	 */
	public String getFormat() {
		return this.format;
	}
	
}
