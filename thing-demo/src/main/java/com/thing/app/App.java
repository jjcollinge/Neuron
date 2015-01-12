package com.thing.app;

import com.thing.api.components.ServiceContainer;
import com.thing.registration.RegistrationManager;
import com.thing.sessions.SessionManager;
import com.thing.storage.MongoDBDeviceDAO;

public class App {

	public static void main(String[] args) {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.clear();
		
		ServiceContainer container = new ServiceContainer();
		
		container.addService(SessionManager.getInstance());
		container.addService(RegistrationManager.getInstance());
		
		container.startServices();
		
	}

}
