package com.neuron.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.neuron.api.components.Request;
import com.neuron.api.components.Response;
import com.neuron.api.components.Serializer;
import com.neuron.api.connectors.ProtocolAdapter;
import com.neuron.api.data.Payload;

public class MqttAdapter extends ProtocolAdapter implements MqttCallback {

	private static final Logger log = Logger.getLogger(MqttAdapter.class
			.getName());

	private MqttAsyncClient client;
	private final String protocol = "tcp";
	
	/**
	 * Stored connection information for re-connect
	 */
	private String host;
	private int port;

	public MqttAdapter() {
	}

	/**
	 * Connect to message broker / server
	 * @param host
	 * @param port
	 */
	public void connect(String host, int port) {
		
		this.host = host;
		this.port = port;
		
		final String address = protocol + "://" + host + ":" + String.valueOf(port);
		
		try {
			client = new MqttAsyncClient(address, MqttClient.generateClientId());
			client.connect().waitForCompletion(1000);
			client.setCallback(this);
			log.log(Level.INFO, "Connected to Mqtt broker " + address);
		} catch (MqttException e) {
			log.log(Level.WARNING, "Couldn't connect to Mqtt broker " + address);
		}
	}

	/**
	 * Disconnect from Mqtt broker
	 */
	public void disconnect() {

		try {
			log.log(Level.INFO, "Disconnected from broker");
			client.disconnect();
		} catch (MqttException e) {
			log.log(Level.WARNING, "Failed to disconnect from broker");
		}
	}


	/**
	 * Send data to Mqtt broker
	 * ---------------------------------
	 * Expected format : { "data": "..."}
	 */
	public void send(Response response) {

		// Check connector is connected to broker
		if (!isConnected()) {
			log.log(Level.WARNING,
					"Cannot send message as Connector is not connected!");
			return;
		}

		// extract response components
		Payload payload = response.getPayload();
		
		ArrayList<String> messages = new ArrayList<String>();
		
		// format payload into required formats
		for(String format : response.getFormats()) {
			messages.add(Serializer.serialize(format, payload));
		}
		
		// default
		String qos = "2";
		qos = response.getHeader("qos");
		String topic = response.getHeader("topic");
		
		if(topic == null) {
			log.log(Level.INFO, "Dropping response as topic was not set!");
			return;
		}

		// send a new message for each format
		for(int i = 0; i < messages.size(); i++) {
			// Pack message into MqttMessage
			MqttMessage msg = new MqttMessage();
			msg.setPayload(messages.get(i).getBytes());

			// Publish message on topic
			try {
				client.publish(topic, msg);
				log.log(Level.INFO, "Sent message " + payload.getPayload().toString() + " on topic "
						+ topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "An exception has been thrown", e);
			}
		}
	}

	/**
	 * Subscribe to Mqtt topic
	 */
	public void subscribe(String topic, int qos) {

		if (client.isConnected()) {
			try {
				client.subscribe(topic, qos);
				log.log(Level.INFO, "Subscribed to topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "Couldn't subscribe to topic", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't subscribe to topic " + topic
					+ " as client is not connected to broker");
		}
	}

	/**
	 * Unsubscribe from Mqtt topic
	 */
	public void unsubscribe(String topic) {

		// check the connector is connected to the broker
		if (client.isConnected()) {
			try {
				client.unsubscribe(topic);
				log.log(Level.INFO, "Unsubscribed from topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "Couldn't unsubscribe from topic", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't unsubscribe to topic " + topic
					+ " as client is not connected to broker");
		}
	}

	/**
	 * Check client connectivity
	 */
	public boolean isConnected() {
		return client.isConnected();
	}

	/**
	 * Mqtt Callback methods
	 */
	
	/**
	 * Called on connection lost
	 */
	public void connectionLost(Throwable e) {
		log.log(Level.INFO, "Connection to broker has been lost", e);
		connect(this.host, this.port);
	}

	/**
	 * Called on delivery of message
	 */
	public void deliveryComplete(IMqttDeliveryToken e) {
		log.log(Level.FINE, "Delivery of message complete");
	}

	/**
	 * Called on receipt of message
	 */
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {

		// New MqttMessage has arrived. Traffic is already filtered by topics so
		// forward all incoming data
		
		String messageString = message.toString();

		log.log(Level.INFO, "Received new message " + messageString
				+ " on topic " + topic);

		// Pack message into internal message
		Request request = new Request();
		request.setData(messageString);
		request.setProtocol("mqtt");
		request.addHeader("topic", topic);
		
		notifyListeners(request);
	}


}
