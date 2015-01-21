package com.thing.app;

import com.thing.api.components.ServiceContainer;
import com.thing.registration.RegistrationController;
import com.thing.sessions.SessionController;
import com.thing.storage.MongoDBDeviceDAO;

public class App {

	public static void main(String[] args) {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.clear();
		
		ServiceContainer container = new ServiceContainer();
		
		container.addService(SessionController.getInstance());
		container.addService(new RegistrationController());
		
		container.startServices();
		
	}

}
