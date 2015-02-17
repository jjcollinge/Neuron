package com.neuron.app.adapters.mock;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.response.Response;

public class MqttAdapterMock extends Adapter {

	private static Response lastResponse;
	
	public void connect(String host, int port) {
		// TODO Auto-generated method stub
		
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	public void send(Response response) {
		// TODO Auto-generated method stub
		lastResponse = response;
	}

	public void subscribe(String topic, int qos) {
		// TODO Auto-generated method stub
		
	}

	public void unsubscribe(String topic) {
		// TODO Auto-generated method stub
		
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Response getLastResponse() {
		return lastResponse;
	}

}
