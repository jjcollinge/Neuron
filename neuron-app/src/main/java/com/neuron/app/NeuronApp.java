package com.neuron.app;

import com.neuron.api.components.Application;
import com.neuron.api.components.services.ServiceContainer;
import com.neuron.registration.RegistrationRequestHandler;
import com.neuron.sessions.SessionController;
import com.neuron.web.WebController;

/**
 * The main application which is required to register
 * the implementation specific details of the data
 * access object and any supported messengers. Any
 * valid application must then call setup to check if
 * the supplied class files and configuration files
 * can be successfully loaded. Finally services are
 * added to a service container and then all services
 * are started simultaneously.
 * @author JC
 *
 */
public class NeuronApp {
	
	/**
	 * This class exists in order to separate the setup
	 * sequence of the application from the main
	 * method. This allows the application to have
	 * multiple main methods in case you want to handle
	 * command lines arguments differently.
	 * @author JC
	 *
	 */
	private static class Neuron extends Application implements Runnable {
		
		/**
		 * Application startup sequence
		 */
		public void run() {
			
			// Ideally these would be loaded from the class loader at runtime
			registerDAOClassName("com.neuron.dal.MongoDBDeviceDAO");
			registerMessengerClassName("com.neuron.messaging.MqttAdapter");
			registerProxyClassName("com.neuron.rest.MqttDeviceProxy");
			
			ServiceContainer container = null;
			
			if(setup("neuron-config.json", System.getenv("NEURON__HOME"))) {
			
				/**
				 * Create a service container to handle the setup and tear down of services
				 */
				container = new ServiceContainer(getConfig());	
				container.addService(SessionController.getSingleton());
				container.addService(new RegistrationRequestHandler());
				container.addService(WebController.getInstance());
				container.startServices();
			}
		
		}
	}
	
	/**
	 * Main entry point of program, used to read any command line args
	 * and set the relevant properties. This can be overloaded if
	 * required. An instance of Neuron must be started at the end of
	 * any main function in a new thread.
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		// Start application thread
		new Thread(new Neuron()).start();
	}

}
