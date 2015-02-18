package com.neuron.app.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.events.DataEvent;
import com.neuron.api.model.Payload;
import com.neuron.api.proxy.DeviceProxy;
import com.neuron.api.request.Request;
import com.neuron.api.response.Response;

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
	private Adapter adapter;
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
		AdapterFactory factory =  AdapterFactory.getFactory();
		adapter = factory.getAdapter("mqtt");
		adapter.addRequestListener(this);
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
			adapter.subscribe(topic + "/stream/response", 2);
			Payload payload = new Payload("START_STREAM");
			Response res = new Response(payload);
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			adapter.send(res);
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
			adapter.unsubscribe(topic + "/stream/response");
			Payload payload = new Payload("STOP_STREAM");
			Response res = new Response(payload);
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			adapter.send(res);
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
			Payload payload = new Payload(option);
			Response res = new Response(payload);
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			adapter.send(res);
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
			Payload payload = new Payload(String.valueOf(refreshRate));
			Response res = new Response(payload);
			res.addFormat("JSON");
			res.addFormat("MQTT");
			res.addHeader("topic", topic);
			res.addHeader("qos", "2");
			adapter.send(res);
		} else {
			notReady();
		}
	}

}
