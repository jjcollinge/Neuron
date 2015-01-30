package com.neuron.api.data;

/**
 * Configuration POJO which contains all the information
 * needed to add a new connector to a system dynamically.
 * @see com.neuron.api.components.Application for usage
 * @author JC
 *
 */
public class ConnectorConfiguration {

	private String hostname;
	private int port;
	private String type;
	private String username;
	private String password;
	@SuppressWarnings("rawtypes")
	private Class messengerClass;
	
	public ConnectorConfiguration(String hostname, int port, String type, @SuppressWarnings("rawtypes") Class messengerClass) {
		this.hostname = hostname;
		this.port = port;
		this.type = type;
		this.messengerClass = messengerClass;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getMessengerClass() {
		return this.messengerClass;
	}
}
