package com.neuron.api.components.services;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls a collection of services. Is responsible
 * for ensuring that a services setup method is called
 * before the start method.
 * @author JC
 *
 */
public class ServiceContainer {

	private static final Logger log = Logger.getLogger(ServiceContainer.class
			.getName());

	private LinkedHashMap<Service, Boolean> services;
	private boolean running;

	/**
	 * Initialise collections
	 */
	public ServiceContainer() {
		this.services = new LinkedHashMap<Service, Boolean>();
		this.running = false;
	}

	/**
	 * Add a new services to the service collection.
	 * Will ONLY succeed if the services are stopped.
	 * @param service to add
	 */
	public void addService(Service service) {
		if (!running)
			services.put(service, false);
	}

	/**
	 * Remove a current service from the service
	 * collection. Will ONLY succeed if the services
	 * are stopped.
	 * @param service to remove
	 */
	public void removeService(Service service) {
		if (!running)
			services.remove(service);
	}

	/**
	 * Start all services currently in the service
	 * collection. Must start from a stopped state.
	 */
	public void startServices() {
		if(!running) {
			for (Service service : services.keySet()) {
				service.setup();
				service.start();
				services.put(service, true);
			}
			log.log(Level.INFO, this.toString());
			running = true;
		}
	}

	/**
	 * Stop all services currently in the service
	 * collection. Can only stop services if
	 * the services are already running.
	 */
	public void stopServices() {
		if(running) {
			for (Service service : services.keySet()) {
				service.stop();
				services.put(service, false);
			}
			log.log(Level.INFO, this.toString());
			running = false;
		}
	}

	/**
	 * Produces a human readable representation of the
	 * running services.
	 */
	public String toString() {
		String tmp = "";
		tmp += "\n-------------------------------------\n";
		for (Service service : services.keySet()) {
			tmp += "Service: " + service.getClass().getSimpleName() + " is ";
			if (services.get(service)) {
				// running
				tmp += "Running\n";
			} else {
				// not running
				tmp += "Stopped\n";
			}
		}
		tmp += "-------------------------------------\n";
		return tmp;
	}
}
