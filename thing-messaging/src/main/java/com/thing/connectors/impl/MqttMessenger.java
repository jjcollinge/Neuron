package com.thing.connectors.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thing.api.events.MessageEvent;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.connectors.Messenger;

public class MqttMessenger extends Messenger implements MqttCallback {

	private static final Logger log = Logger.getLogger(MqttMessenger.class
			.getName());

	private MqttAsyncClient client;
	private final String protocol = "tcp";

	public MqttMessenger() {
	}

	/**
	 * Connect to the Mqtt broker
	 */
	public void connect(String host, int port) {
		
		String address = protocol + "://" + host + ":" + String.valueOf(port);
		
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
	public void sendMessage(Parcel parcel) {

		// Check connector is connected to broker
		if (!isConnected()) {
			log.log(Level.WARNING,
					"Cannot send message as Connector is not connected!");
			return;
		}

		// extract parcel components
		Message message = parcel.getMessage();
		String messageString = message.getPayload();
		int qos = parcel.getQos();

		// Pack data in expected format device
		String pack = "{ \"data\": \"" + messageString + "\" }";

		// Pack JSON into MqttMessage
		MqttMessage msg = new MqttMessage();
		msg.setPayload(pack.getBytes());

		// Publish message on topic
		try {
			client.publish(parcel.getTopic(), msg);
			log.log(Level.INFO, "Sent message " + pack + " on topic "
					+ parcel.getTopic());
		} catch (MqttException e) {
			log.log(Level.SEVERE, "An exception has been thrown", e);
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

	// Mqtt specfic callbacks
	
	public void connectionLost(Throwable e) {
		log.log(Level.INFO, "Connection to broker has been lost", e);
	}

	public void deliveryComplete(IMqttDeliveryToken e) {
		log.log(Level.FINE, "Delivery of message complete");
	}

	public void messageArrived(String topic, MqttMessage message)
			throws Exception {

		// New MqttMessage has arrived. Traffic is already filtered by topics so
		// forward all incoming data
		
		String messageString = message.toString();
		messageString = "{ \"payload\": \""
				+ messageString.replaceAll("\"", "\\\\\"")
				+ "\", \"format\":\"JSON\", \"protocol\":\"MQTT\" }";
		log.log(Level.INFO, "Received new message " + messageString
				+ " on topic " + topic);

		// Pack message into internal message
		Message msg = null;
		try {
			Gson gson = new Gson();
			msg = gson.fromJson(messageString, Message.class);
		} catch (JsonSyntaxException e1) {
			log.log(Level.INFO,
					"Couldn't transform incoming SERIAL message to internal message");
			return;
		}

		// Notify listeners of new message
		MessageEvent event = new MessageEvent(this, msg);
		notifyListeners(event);
	}

}
