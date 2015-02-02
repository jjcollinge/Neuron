package com.neuron.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.DeviceProxy;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.connectors.ConnectorFactoryImpl;
import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.events.DataEvent;
import com.neuron.api.events.MessageEvent;

/**
 * MQTT implementation of a device proxy. Will use a mqtt
 * connector to speak to the device and handle any mqtt
 * specific validation.
 * @author JC
 *
 */
public class MqttDeviceProxy extends DeviceProxy {

	private static final Logger log = Logger.getLogger(MqttDeviceProxy.class
			.getName());

	private int sessionId;
	private Connector connector;
	private boolean ready;

	public MqttDeviceProxy() {
		ready = false;
	}

	/**
	 * Initialises connection to broker
	 */
	@Override
	public void setup(int sessionId) {
		this.sessionId = sessionId;
		ConnectorFactory factory =  new ConnectorFactoryImpl();
		connector = factory.getConnector("mqtt");
		connector.addMessageEventListener(this);
		ready = true;
	}

	/**
	 * Sends a signal to the device to start streaming its data
	 */
	@Override
	public void startSensorStreaming(int sensorId) {
		if (ready) {
			String topic = "devices/" + sessionId + "/sensors/"
					+ sensorId;
			connector.subscribe(topic + "/stream/response", 2);
			Message msg = new Message("START_STREAM", "MQTT", "JSON");
			connector.send(new Parcel.ParcelBuilder(msg).topic(topic).qos(2).build());
		} else {
			notReady();
		}
	}

	/**
	 * Sends a signal to the device to stop streaming its data
	 */
	@Override
	public void stopSensorStreaming(int sensorId) {
		if (ready) {
			String topic = "devices/" + sessionId + "/sensors/"
					+ sensorId;
			connector.unsubscribe(topic + "/stream/response");
			Message msg = new Message("STOP_STREAM", "MQTT", "JSON");
			connector.send(new Parcel.ParcelBuilder(msg).topic(topic).qos(2).build());
		} else {
			notReady();
		}
	}

	/**
	 * Sends a signal to the device to perform an action
	 */
	@Override
	public void operateActuator(int actuatorId, String option) {
		if (ready) {
			String topic = "devices/" + sessionId + "/actuators/"
					+ actuatorId;
			Message msg = new Message(option, "MQTT", "JSON");
			connector.send(new Parcel.ParcelBuilder(msg).topic(topic).qos(2).build());
		} else {
			notReady();
		}
	}

	/**
	 * Message response received from the device - should only be a data
	 * response from a stream request
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
		log.log(Level.WARNING,
				"DeviceProxy is not ready, please call the setup method first");
	}

}
