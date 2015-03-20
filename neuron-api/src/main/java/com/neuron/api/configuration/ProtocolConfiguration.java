package com.neuron.api.configuration;

/**
 * Configuration POJO which contains all the information
 * needed to add a new connector to a system dynamically.
 * @see com.neuron.api.core.Application for usage
 * @author JC
 *
 */
public class ProtocolConfiguration {

	private String hostname;
	private int port;
	private String type;
	private String username;
	private String password;
	@SuppressWarnings("rawtypes")
	private Class messengerClass;
	
	public ProtocolConfiguration(String hostname, int port, String type, @SuppressWarnings("rawtypes") Class messengerClass) {
		this.hostname = hostname;
		this.port = port;
		this.type = type;
		this.messengerClass = messengerClass;
	}
	
	/**
	 * Set protocol server username
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Set protocol server password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Get protocol server hostname
	 * @return
	 */
	public String getHostname() {
		return this.hostname;
	}
	
	/**
	 * Get protocol server port
	 * @return
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Get protocol server type
	 * @return
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Get protocol server username
	 * @return
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Get protocol server password
	 * @return
	 */
	public String getPassword() {
		return this.password;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getMessengerClass() {
		return this.messengerClass;
	}
}
