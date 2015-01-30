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
	private volatile boolean running;

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
	public void addService(final Service service) {
		log.log(Level.INFO, "Adding new service " + service.getClass().getSimpleName());
		if (!running) {
			service.setup();
			services.put(service, false);
		} else {
			log.log(Level.INFO, "Cannot add a service whilst the container is running");
		}
	}

	/**
	 * Remove a current service from the service
	 * collection. Will ONLY succeed if the services
	 * are stopped.
	 * @param service to remove
	 */
	public void removeService(Service service) {
		log.log(Level.INFO, "Removing service " + service.getClass().getSimpleName());
		if (!running) {
			services.remove(service);
		} else {
			log.log(Level.INFO, "Cannot remove service whilst container is running");
		}
	}

	/**
	 * Start all services currently in the service
	 * collection. Must start from a stopped state.
	 */
	public void startServices() {
		log.log(Level.INFO, "Starting services");
		if(!running) {
			for(Service service : services.keySet()) {
				service.start();
				services.put(service, true);
			}
			log.log(Level.INFO, this.toString());
			running = true;
		} else {
			log.log(Level.INFO, "Services are already running");
		}
	}

	/**
	 * Stop all services currently in the service
	 * collection. Can only stop services if
	 * the services are already running.
	 */
	public void stopServices() {
		log.log(Level.INFO, "Stopping services");
		if(running) {
			for (Service service : services.keySet()) {
				service.stop();
				services.put(service, false);
			}
			log.log(Level.INFO, this.toString());
			running = false;
		} else {
			log.log(Level.INFO, "Cannot stop services as they are not running");
		}
	}

	/**
	 * Produces a human readable representation of the
	 * running services.
	 */
	public String toString() {
		log.log(Level.INFO, "Printing services");
		String tmp = "";
		tmp += "\n#####################################\n";
		for(Service service : services.keySet()) {
			tmp += "Service name: " + service.getClass().getSimpleName() + "\n";
			tmp += "Service status: ";
			if(services.get(service) == true){
				tmp += " RUNNING\n";
			} else {
				tmp += " STOPPED\n";
			}
			tmp += "-------------------------------------\n";
		}
		tmp += "#####################################\n";
		return tmp;
	}
}
