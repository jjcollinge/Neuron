package com.thing.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.SessionDAO;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.events.DataEvent;
import com.neuron.api.events.MessageEvent;

public class MqttDeviceProxy extends DeviceProxy {

	private static final Logger log = Logger.getLogger(MqttDeviceProxy.class
			.getName());
	
	private Session session;
	private Connector connector;
	private boolean ready;

	public MqttDeviceProxy() {
		ready = false;
	}
	/**
	 * Initialises connetion to broker
	 */
	@Override
	public void setup(int sessionId) {
		AbstractDAOFactory sessionFactory = DAOFactoryProducer.getFactory("session");
		SessionDAO dao = sessionFactory.getSessionDAO("mongodb");
		session = dao.get(sessionId);
		ConnectorFactory factory =  new ConnectorFactoryImpl();
		connector = factory.getConnector((String)session.getProperty("protocol"));
		connector.addMessageEventListener(this);
		ready = true;
	}
	
	/**
	 * Sends a signal to the device to start streaming its data
	 */
	@Override
	public void startSensorStreaming(int sensorId) {
		if(ready) {
			String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
			connector.subscribe(topic + "/stream/response", 2);
			connector.send(ParcelPacker.makeParcel("START_STREAM", "JSON",
					topic, "MQTT"));
		} else {
			notReady();
		}
	}
	
	/**
	 * Sends a signal to the device to stop streaming its data
	 */
	@Override
	public void stopSensorStreaming(int sensorId) {
		if(ready) {
			String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
			connector.unsubscribe(topic + "/stream/response");
			connector.send(ParcelPacker.makeParcel("STOP_STREAM", "JSON",
					topic, "MQTT"));
		} else {
			notReady();
		}
	}
	
	/**
	 * Sends a signal to the device to perform an action
	 */
	@Override
	public void operateActuator(int actuatorId, String option) {
		if(ready) {
			String topic = "devices/" + session.getId() + "/actuators/" + actuatorId;
			connector.send(ParcelPacker.makeParcel(option, (String) session.getProperty("format"), topic, (String) session.getProperty("protocol")));
		} else {
			notReady();
		}
	}
	
	/**
	 * Message response received from the device - should only be a data response from a stream request
	 */
	@Override
	public void onMessageArrived(MessageEvent event) {
		String payload = event.getMessage().getPayload();
		DataEvent dataEvent = new DataEvent(this, payload);
		notifyListeners(dataEvent);
	}
	
	/**
	 * Log that the device has not been setup correctly
	 */
	private void notReady() {
		log.log(Level.WARNING, "DeviceProxy is not ready, please call the setup method first");
	}

}
