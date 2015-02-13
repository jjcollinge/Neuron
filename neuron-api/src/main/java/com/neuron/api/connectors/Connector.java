package com.neuron.api.connectors;

import com.neuron.api.components.Response;
import com.neuron.api.events.RequestListener;

/**
 * A connector wraps an adapter to facilitate
 * additional functionality to be added without
 * affecting the adapter implementations.
 * @author JC
 *
 */
public class Connector {

	private ProtocolAdapter adapter;
	
	/**
	 * Set the messenger to use in this connector
	 * @param messenger The messenger for this connector to use
	 */
	public void setMessenger(ProtocolAdapter messenger) {
		this.adapter = messenger;
	}
	
	/**
	 * Connect to to a given server
	 * @param host The server hostname
	 * @param port The server port
	 */
	public void connect(String host, int port) {
		adapter.connect(host, port);
	}
	
	/**
	 * Disconnect from the current server
	 */
	public void disconnect() {
		adapter.disconnect();
	}
	
	/**
	 * Send a given response to the server
	 * @param response The response to send
	 */
	public void send(Response response) {
		adapter.send(response);
	}
	
	/**
	 * Listen to all messages on a given topic
	 * Ensure that the given QOS is met
	 * @param topic  The topic to listen to
	 * @param qos The qos to enforce
	 */
	public void subscribe(String topic, int qos) {
		adapter.subscribe(topic, qos);
	}
	
	/**
	 * Stop listening to messages on a give topic
	 * @param topic The topic to stop listening to
	 */
	public void unsubscribe(String topic) {
		adapter.unsubscribe(topic);
	}
	
	/**
	 * State of current server connection
	 * @return boolean The connection state
	 */
	public boolean isConnected() {
		return adapter.isConnected();
	}
	
	/**
	 * Add a new listener to receive messages collected
	 * by the messenger
	 * @param requestListener The new listener
	 */
	public void addRequestListener(RequestListener requestListener) {
		adapter.addRequestListener(requestListener);
	}
	
	/**
	 * Remove a current listener from receiving messages collected
	 * by the messenger
	 * @param requestListener The current listener
	 */
	public void removeRequestListener(RequestListener requestListener) {
		adapter.removeRequestListener(requestListener);
	}
		
}
