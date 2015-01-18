package com.thing.registration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

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

	private RegistrationValidator validator;
	private Message request;

	/**
	 * Set any data that will be required to perform a 
	 * successful run of the doWork method.
	 * @param message
	 */
	public RegistrationWorker(Message message) {
		request = message;
		validator = new RegistrationValidator();
	}

	/**
	 * This will be ran by a call to runnable in the super class
	 */
	protected void doWork() {

		String payload = request.getPayload();
		String format = request.getFormat();
		String protocol = request.getProtocol();

		if (format.equals("JSON")) {

			// validate against schema
			if (!validator.isValid(payload))
				return;

			// Map the registration request onto a POJO
			ObjectMapper mapper = new ObjectMapper();
			Registration registration = null;
			
			try {
				registration = mapper.readValue(payload, Registration.class);
			} catch (Exception e) {
				log.log(Level.INFO, "Dropped corrupt registration", e);
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
		} // else other supported formats (XML)
	}
}
