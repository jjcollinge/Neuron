package com.neuron.api.data;

public class Context {

	private String protocol;	// protocol to communicate
	private String format;		// format of messages
 
	/**
	 * Holds the information about a current situation
	 * @param protocol
	 * @param format
	 */
	public Context(String protocol, String format) {
		this.protocol = protocol;
		this.format = format;
	}
	
	/**
	 * Get the protocol from the context
	 * @return
	 */
	public String getProtocol() {
		return this.protocol;
	}
	
	/**
	 * Get the format from the context
	 * @return
	 */
	public String getFormat() {
		return this.format;
	}
	
}
