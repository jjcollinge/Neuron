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
import com.thing.api.events.MessageEventProducer;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.Connector;

public class MqttConnector extends BaseConnector implements MqttCallback {

	private static final Logger log = Logger.getLogger(MqttConnector.class
			.getName());

	private MqttAsyncClient client;
	private final String protocol = "tcp";

	public MqttConnector() {
		
	}

	public void connect(String host, String port) {

		try {
			String address = protocol + "://" + host + ":" + port;
			client = new MqttAsyncClient(address, MqttClient.generateClientId());
			client.connect().waitForCompletion(1000);
			client.setCallback(this);
			log.log(Level.INFO, "Connected to Mosquitto broker " + address);
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	public void disconnect() {

		try {
			log.log(Level.INFO, "Disconnected from broker");
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

	// Mqtt Device expects data in the following format: { "data": "..." } on
	// topic
	public void sendMessage(Parcel parcel) {

		if (!isConnected()) {
			log.log(Level.WARNING,
					"Cannot send message as Connector is not connected!");
			return;
		}

		Message m = parcel.getMessage();
		String messageString = m.getPayload();

		// Pack for device
		String pack = "{ \"data\": \"" + messageString + "\" }";

		// Convert POJO to JSON
		String jsonMessage = null;
		try {
			Gson gson = new Gson();
			jsonMessage = gson.toJson(pack);
			jsonMessage = jsonMessage.substring(1, jsonMessage.length() - 1)
					.replace("\\\"", "\"");
		} catch (JsonSyntaxException e) {
			log.log(Level.SEVERE,
					"Couldn't transform outgoing message to JSON format");
			return; // drop troublesome message
		}

		// Pack JSON into MqttMessage
		MqttMessage msg = new MqttMessage();
		msg.setPayload(jsonMessage.getBytes());

		// Publish to topic
		try {
			client.publish(parcel.getTopic(), msg);
			log.log(Level.INFO, "Sent message " + jsonMessage + " on topic "
					+ parcel.getTopic());
		} catch (MqttException e) {
			log.log(Level.SEVERE, "An exception has been thrown", e);
		}

	}

	public void subscribe(String topic, int qos) {

		if (client.isConnected()) {
			try {
				client.subscribe(topic, qos);
				log.log(Level.INFO, "Subscribed to topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "An exception has been thrown", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't subscribe to topic " + topic
					+ " as client is not connected to broker");
		}

	}

	public void unsubscribe(String topic) {

		if (client.isConnected()) {
			try {
				client.unsubscribe(topic);
				log.log(Level.INFO, "Unsubscribed to topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "An exception has been thrown", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't unsubscribe to topic " + topic
					+ " as client is not connected to broker");
		}

	}

	public boolean isConnected() {

		return client.isConnected();

	}

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
		this.notifyListeners(event);

	}

}
