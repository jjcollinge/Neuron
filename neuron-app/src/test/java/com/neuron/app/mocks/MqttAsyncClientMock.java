package com.neuron.app.mocks;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttAsyncClientMock extends MqttAsyncClient {

	private static String lastMessage;
	
	public MqttAsyncClientMock(String serverURI, String clientId)
			throws MqttException {
		super(serverURI, MqttClient.generateClientId());
	}
	
	public IMqttDeliveryToken publish(String topic, MqttMessage message) {
		lastMessage = new String(message.getPayload());
		return null;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}
	
	public void resetLastMessage() {
		lastMessage = null;
	}

}
