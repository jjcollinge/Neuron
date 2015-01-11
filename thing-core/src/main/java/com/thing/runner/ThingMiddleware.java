package com.thing.runner;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.ServiceContainer;
import com.thing.registration.RegistrationManager;
import com.thing.sessions.SessionManager;
import com.thing.storage.DataHandler;
import com.thing.storage.TestClass;

public class ThingMiddleware {

	public static void main(String[] args) {
		
		Thread master = new Thread(new Runner());
		
		master.start();
		
	}

}

class Runner implements Runnable {

	private static final Logger log = Logger.getLogger( Runner.class.getName() );
	
	public void run() {
		
		log.log(Level.INFO, "Started running application");
		
//		LogManager.getLogManager().reset();
//        Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
//        globalLogger.setLevel(java.util.logging.Level.OFF);
		
		DataHandler.getInstance().clearDevices();
		
		ServiceContainer container = new ServiceContainer();
		container.addService(SessionManager.getInstance());
		container.addService(RegistrationManager.getInstance());
		
		container.startServices();
		
		//TestClass test = new TestClass();
		
	}
	
}
