package com.thing.app;

import com.thing.api.components.ServiceContainer;
import com.thing.registration.RegistrationManager;
import com.thing.sessions.SessionManager;
import com.thing.storage.DataHandler;

public class App {

	public static void main(String[] args) {
		
		DataHandler dao = DataHandler.getInstance();
		dao.clearDevices();
		
		ServiceContainer container = new ServiceContainer();
		
		container.addService(SessionManager.getInstance());
		container.addService(RegistrationManager.getInstance());
		
		container.startServices();
		
	}

}
