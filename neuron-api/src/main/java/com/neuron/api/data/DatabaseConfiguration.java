package com.neuron.api.data;

public class DatabaseConfiguration {

	private String hostname;
	private int port;
	private String type;
	private String username;
	private String password;
	private String databaseName;
	private Class clientClass;
	
	public DatabaseConfiguration(String hostname, int port, String type, String databaseName, Class clientClass) {
		this.hostname = hostname;
		this.port = port;
		this.type = type;
		this.databaseName = databaseName;
		this.clientClass = clientClass;
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
	
	public String getDatabaseName() {
		return this.databaseName;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public Class getClientClass() {
		return this.clientClass;
	}
}
