package com.thing.registration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.RequestResponseWorker;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Device;
import com.thing.api.model.Session;
import com.thing.registration.model.Registration;
import com.thing.sessions.SessionController;
import com.thing.storage.MongoDBDeviceDAO;

public class RegistrationWorker extends RequestResponseWorker {

	private static final Logger log = Logger.getLogger(RegistrationWorker.class
			.getName());

	private Message request;

	/**
	 * Set any data that will be required to perform a 
	 * successful run of the doWork method.
	 * @param message
	 */
	public RegistrationWorker(Message message) {
		request = message;
	}

	/**
	 * This will be ran by a call to runnable in the super class
	 */
	protected void doWork() {

		String payload = request.getPayload();
		String format = request.getFormat();
		String protocol = request.getProtocol();

		RegistrationFactory factory = new RegistrationFactory();
		Registration registration = factory.getRegistration(format, payload);
		
		if(registration == null) {
			log.log(Level.INFO, "Dropping corrupt registration request");
			return;
		}
		
		// Create model
		Session session = new Session();
		session.addProperty("protocol", protocol);
		session.addProperty("format", format);
		Device device = registration.getDevice();
		device.setSessionId(session.getId());

		// Track device activity
		SessionController.getInstance().addSession(session);

		// Store device
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.insert(device);

		// Create response
		String returnTopic = registration.getReturnAddress();
		Message res = new Message(String.valueOf(device.getSessionId()),
				format, protocol);
		
		// Set response
		setResponse(ParcelPacker.makeParcel(res, returnTopic));
		log.log(Level.INFO, "Successfully registered device");
	}
}
