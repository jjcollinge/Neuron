package com.thing.api.components;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceContainer {
	
	private static final Logger log = Logger.getLogger(ServiceContainer.class
			.getName());
	
	LinkedHashMap<Service, Boolean> services;
	boolean running;
	
	public ServiceContainer() {
		services = new LinkedHashMap<Service, Boolean>();
		running = false;
	}
	
	public void addService(Service service) {
		if(!running)
			services.put(service, false);
	}
	
	public void removeService(Service service) {
		if(!running)
			services.remove(service);
	}
	
	public void startServices() {
		for(Service service : services.keySet()) {
			service.start();
			services.put(service, true);
		}
		log.log(Level.INFO, this.toString());
		running = true;
	}
	
	public void stopServices() {
		for(Service service : services.keySet()) {
			service.stop();
			services.put(service, false);
		}
		log.log(Level.INFO, this.toString());
		running = false;
	}
	
	public String toString() {
		String tmp = "";
		tmp += "\n-------------------------------------\n";
		for(Service service : services.keySet()) {
			tmp += "Service: " + service.getClass().getSimpleName() + " is ";
			if(services.get(service)) {
				//running
				tmp += "Running\n";
			} else {
				//not running
				tmp += "Stopped\n";
			}
		}
		tmp += "-------------------------------------\n";
		return tmp;
	}
}
