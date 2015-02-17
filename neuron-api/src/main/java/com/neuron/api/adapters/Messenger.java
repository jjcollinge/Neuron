package com.neuron.api.adapters;

import com.neuron.api.response.Response;

/**
 * The API for any messenger implementations
 * @author JC
 *
 */
public interface Messenger {

	/**
	 * Connect to to a given server
	 * @param host The server hostname
	 * @param port The server port
	 */
	public void connect(String host, int port);
	/**
	 * Disconnect from the current server
	 */
	public void disconnect();		
	/**
	 * Send a given response to the server
	 * @param response The response to send
	 */
	public void send(Response response);
	/**
	 * Listen to all messages on a given topic
	 * Ensure that the given QOS is met
	 * @param topic  The topic to listen to
	 * @param qos The qos to enforce
	 */
	public void subscribe(String topic, int qos);
	/**
	 * Stop listening to messages on a give topic
	 * @param topic The topic to stop listening to
	 */
	public void unsubscribe(String topic);
	/**
	 * State of current server connection
	 * @return boolean The connection state
	 */
	public boolean isConnected();
	
}
