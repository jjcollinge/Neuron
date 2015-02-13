package com.neuron.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.DeviceProxy;
import com.neuron.api.components.Request;
import com.neuron.api.components.Response;
import com.neuron.api.connectors.Connector;
import com.neuron.api.connectors.ConnectorFactory;
import com.neuron.api.events.DataEvent;

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
		ConnectorFactory factory =  new ConnectorFactory();
		connector = factory.getConnector("mqtt");
		connector.addRequestListener(this);
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
			Response res = new Response("START_STREAM");
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			connector.send(res);
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
			Response res = new Response("STOP_STREAM");
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			connector.send(res);
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
			Response res = new Response(option);
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			connector.send(res);
		} else {
			notReady();
		}
	}

	/**
	 * Message response received from the device - should only be a data
	 * response from a stream request
	 */
	@Override
	public void onRequest(Request req) {
		String payload = (String) req.getData();
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

	@Override
	public void configureDevice(int refreshRate) {
		if (ready) {
			String topic = "devices/" + sessionId + "/configure";
			Response res = new Response(String.valueOf(refreshRate));
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			connector.send(res);
		} else {
			notReady();
		}
	}

}
