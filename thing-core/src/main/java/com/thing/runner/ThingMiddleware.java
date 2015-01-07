package com.thing.runner;

import com.registration.RegistrationManager;
import com.thing.api.components.ServiceContainer;
import com.thing.management.DeviceManager;
import com.thing.messaging.MessagingService;
import com.thing.sessions.SessionManager;

public class ThingMiddleware {

	public static void main(String[] args) {
		
		Thread master = new Thread(new Runner());
		
		master.start();
		
	}

}

class Runner implements Runnable {

	public void run() {
		
		ServiceContainer container = new ServiceContainer();
		container.addService(MessagingService.getInstance());
		container.addService(DeviceManager.getInstance());
		container.addService(RegistrationManager.getInstance());
		
		container.startServices();
		
	}
	
}
