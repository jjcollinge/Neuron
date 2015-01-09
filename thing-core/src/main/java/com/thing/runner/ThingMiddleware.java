package com.thing.runner;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.ServiceContainer;
import com.thing.management.SessionManager;
import com.thing.registration.RegistrationManager;

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
		
		ServiceContainer container = new ServiceContainer();
		container.addService(SessionManager.getInstance());
		container.addService(RegistrationManager.getInstance());
		
		container.startServices();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		new Thread(new MockClient()).start();;
		
	}
	
}
