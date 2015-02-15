package com.neuron.api.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.neuron.api.configuration.Configuration;


/**
 * Required API for any Service implementations
 * @author JC
 *
 */
public abstract class Activity implements Service {

	private HashMap<String, Service> services;
	private String name;
	private Boolean running;
	
	public Activity(String name) {
		services = new HashMap<String, Service>();
		this.name = name;
		this.running = false;
	}
	
	public String getName() {
		return this.name;
	}

	/**
	 * Initialise anything that needs to be done
	 * before the call to start is made.
	 */
	public abstract void setup(Configuration config);

	/**
	 * Start the service
	 */
	public void start() {
		for(Service service : getServices()) {
			service.start();
		};
		running = true;
	}

	/**
	 * Stop the service
	 */
	public void stop() {
		for(Service service : getServices()) {
			service.stop();
		};
		running = false;
	}
	
	/**
	 * Add a sub service
	 * @param service
	 */
	public void addService(String serviceName, Service service) {
		services.put(serviceName, service);
	}
	
	/**
	 * Get a sub services
	 * @param serviceName
	 * @return
	 */
	public Service getService(String serviceName) {
		return services.get(serviceName);
	}
	
	/**
	 * Get all services
	 * @return
	 */
	public ArrayList<Service> getServices() {
		return new ArrayList<Service>(services.values());
	}
	
	/**
	 * Get current running status
	 * @return
	 */
	public Boolean isAlive() {
		return this.running;
	}
}
