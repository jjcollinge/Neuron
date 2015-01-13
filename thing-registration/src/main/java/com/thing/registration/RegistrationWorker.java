package com.thing.registration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.thing.api.components.IdGenerator;
import com.thing.api.components.Worker;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Device;
import com.thing.api.model.Session;
import com.thing.registration.model.Registration;
import com.thing.sessions.SessionManager;
import com.thing.storage.MongoDBDeviceDAO;

public class RegistrationWorker extends Worker {

	private static final Logger log = Logger.getLogger(RegistrationWorker.class
			.getName());

	private RegistrationValidator validator;
	private Message request;
	private Parcel response;

	public RegistrationWorker(Message message) {
		request = message;
		validator = new RegistrationValidator();
	}

	protected void doWork() {

		String payload = request.getPayload();
		String format = request.getFormat();
		String protocol = request.getProtocol();

		if (format.equals("JSON")) {

			// validate against schema
			if (!validator.isValid(payload))
				return;

			ObjectMapper mapper = new ObjectMapper();

			Registration registration = null;
			try {
				registration = mapper.readValue(payload, Registration.class);
			} catch (Exception e) {
				e.printStackTrace();
			} 

			if (registration == null) {
				log.log(Level.INFO, "Dropped corrupt registration");
				return;
			}

			// Create model
			Session session = new Session();
			session.addProperty("protocol", protocol);
			session.addProperty("format", format);
			Device device = registration.getDevice();
			device.setSessionId(session.getId());

			// Track device activity
			SessionManager.getInstance().addSession(session);

			// Store device
			MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
			dao.insert(device);

			// Create response
			String returnTopic = registration.getReturnAddress();
			Message res = new Message(String.valueOf(device.getSessionId()),
					format, protocol);
			this.response = ParcelPacker.makeParcel(res, returnTopic);
			finishWork(response);
		} // else other supported formats (XML)
	}

	public void run() {
		doWork();
	}
}
