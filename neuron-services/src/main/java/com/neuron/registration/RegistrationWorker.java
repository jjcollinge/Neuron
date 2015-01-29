package com.neuron.registration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.RequestResponseWorker;
import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.data.Context;
import com.neuron.api.data.Device;
import com.neuron.api.data.Session;
import com.neuron.api.messaging.Message;
import com.neuron.api.messaging.Parcel;
import com.neuron.sessions.SessionController;

public class RegistrationWorker extends RequestResponseWorker {

	private static final Logger log = Logger.getLogger(RegistrationWorker.class
			.getName());

	private Message request;
	private String databaseType;

	/**
	 * Set any data that will be required to perform a 
	 * successful run of the doWork method.
	 * @param message
	 */
	public RegistrationWorker(Message message, String dbType) {
		request = message;
		databaseType = dbType;
	}

	/**
	 * This is an atomic procedure which will is executed once
	 * per worker. The worker will then be terminated after it
	 * has completed the work.
	 */
	protected void doWork() {

		// TODO: add checks for success at each stage
		String payload = request.getPayload();
		String format = request.getFormat();
		String protocol = request.getProtocol();

		RegistrationFactory registrationFactory = new RegistrationFactory();
		Registration registration = registrationFactory.getRegistration(format, payload);
		
		if(registration == null) {
			log.log(Level.INFO, "Dropping corrupt registration request");
			return;
		}
		
		// Create model
		Session session = new Session();
		Context context = new Context(protocol, format);
		session.setContext(context);
		Device device = registration.getDevice();
		device.setSessionId(session.getId());

		// Track device activity
		SessionController.getInstance().addSession(session);

		// Store device
		AbstractDAOFactory daoFactory = DAOFactoryProducer.getFactory("device");
		DeviceDAO dao = daoFactory.getDeviceDAO(databaseType);
		dao.insert(device);

		// Create response
		String returnTopic = registration.getReturnAddress();
		Message res = new Message(String.valueOf(device.getSessionId()),
				protocol, format);
		
		// Set response
		setResponse(new Parcel.ParcelBuilder(res).topic(returnTopic).qos(2).build());
		log.log(Level.INFO, "Successfully registered device");
		
		/* This response will now be passed back to the controller who will be
		   will be responsible for dispatching it */
	}
}
