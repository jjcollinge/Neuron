package com.thing.messaging;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;


public class MqttMessanger extends Messanger implements  MqttCallback {

	private static final Logger log = Logger.getLogger( MqttMessanger.class.getName() );
	
	private MqttAsyncClient client; 
	private final String protocol = "tcp"; 
	
	public MqttMessanger(int id) {
		super("MQTT", id);
	}
	
	@Override
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

	@Override
	public void send(MessagePayload message) {
		
		if(message.getData() == null || message.getTopic() == null) {
			log.log(Level.WARNING, "Message not sent, message payload was not complete");
			return;
		}
		if(!isConnected()) {
			log.log(Level.WARNING, "Message not sent because the messanger is not connected");
			return;
		}
		
		String jsonMessage = null;
		try {
			Gson gson = new Gson();
			jsonMessage = gson.toJson(message);
		} catch(JsonSyntaxException e) {
			log.log(Level.SEVERE, "Couldn't transform outgoing message to JSON format");
			return; //drop troublesome message
		}
		
		MqttMessage msg = new MqttMessage();
		msg.setPayload(jsonMessage.getBytes());
		
		try {
			client.publish(message.getTopic(), msg);
			log.log(Level.INFO, "Sent message " + jsonMessage + " on topic " + message.getTopic());
		} catch (MqttException e) {
			log.log(Level.SEVERE, "An exception has been thrown", e);
		}
		
	}

	@Override
	public void subscribe(String topic, int qos) {
		
		if(client.isConnected()) {
			try {
				client.subscribe(topic, qos);
				log.log(Level.INFO, "Subscribed to topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "An exception has been thrown", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't subscribe to topic " + topic + " as client is not connected to broker");
		}
		
	}

	@Override
	public void unsubscribe(String topic) {
		
		if(client.isConnected()) {
			try {
				client.unsubscribe(topic);
				log.log(Level.INFO, "Unsubscribed to topic " + topic);
			} catch (MqttException e) {
				log.log(Level.SEVERE, "An exception has been thrown", e);
			}
		} else {
			log.log(Level.WARNING, "Couldn't unsubscribe to topic " + topic + " as client is not connected to broker");
		}
	}

	@Override
	public boolean isConnected() {
		
		return client.isConnected();
		
	}

	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String messageString = message.toString();
		messageString = messageString.replaceAll("\"", "\\\\\"");
		messageString = "{\"topic\":\"" + topic + "\",\"data\":\"" + messageString + "\", \"qos\":2, \"protocol\":\"MQTT\", \"format\":\"JSON\"}";
		messageString = "{\"messangerId\":" + this.getId() + ",\"payload\":" + messageString + "}";
		
		log.log(Level.INFO, "Received new message " + messageString + " on topic " + topic);
		
		Message m = null;
		try {
			Gson gson = new Gson();
			m = gson.fromJson(messageString, Message.class);
		} catch(JsonSyntaxException e) {
			log.log(Level.SEVERE, "Couldn't transform incoming message to internal Message type");
			// drop troublesome message
			return;
		}
		
		MessageEvent event = new MessageEvent(m);
		fireChangeEvent(event);
	}

}
