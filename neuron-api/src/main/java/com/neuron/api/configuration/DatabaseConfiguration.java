package com.neuron.api.configuration;

/**
 * Configuration POJO which contains all the information
 * needed to add the database configuration to the system
 * dynamically.
 * @see com.neuron.api.core.Application for usage
 * @author JC
 *
 */
public class DatabaseConfiguration {

	private String hostname;
	private int port;
	private String type;
	private String username;
	private String password;
	private String databaseName;
	@SuppressWarnings("rawtypes")
	private Class clientClass;
	
	@SuppressWarnings("rawtypes")
	public DatabaseConfiguration(String hostname, int port, String type, String databaseName, Class clientClass) {
		this.hostname = hostname;
		this.port = port;
		this.type = type;
		this.databaseName = databaseName;
		this.clientClass = clientClass;
	}
	
	/**
	 * Set database username
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Set database password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Get database hostname
	 * @return
	 */
	public String getHostname() {
		return this.hostname;
	}

	/**
	 * Get database port
	 * @return
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Get database type
	 * @return
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Get database name
	 * @return
	 */
	public String getDatabaseName() {
		return this.databaseName;
	}
	
	/**
	 * Get database username
	 * @return
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Get database password
	 * @return
	 */
	public String getPassword() {
		return this.password;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getClientClass() {
		return this.clientClass;
	}
}
