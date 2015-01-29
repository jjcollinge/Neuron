package com.neuron.api.data;

public class ConnectorConfiguration {

	private String hostname;
	private int port;
	private String type;
	private String username;
	private String password;
	private Class messengerClass;
	
	public ConnectorConfiguration(String hostname, int port, String type, Class messengerClass) {
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
	
	public Class getMessengerClass() {
		return this.messengerClass;
	}
}
