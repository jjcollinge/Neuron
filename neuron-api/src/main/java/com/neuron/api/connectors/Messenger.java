package com.neuron.api.connectors;

import com.neuron.api.response.Response;

/**
 * The API for any messenger implementations
 * @author JC
 *
 */
public interface Messenger {

	public void connect(String host, int port);
	public void disconnect();		
	public void send(Response response);
	public void subscribe(String topic, int qos);
	public void unsubscribe(String topic);
	public boolean isConnected();
	
}
