package com.neuron.api.connectors;

import com.neuron.api.data.Parcel;
import com.neuron.api.events.MessageEventListener;

/**
 * A connector wraps a messenger to facilitate
 * additional functionality to be added without
 * affecting the messenger implementations.
 * @author JC
 *
 */
public class Connector {

	private Messenger messenger;
	
	/**
	 * Set the messenger to use in this connector
	 * @param messenger
	 */
	public void setMessenger(Messenger messenger) {
		this.messenger = messenger;
	}
	
	/**
	 * Connect to to a given server
	 * @param hostname
	 * @param port
	 */
	public void connect(String host, int port) {
		messenger.connect(host, port);
	}
	
	/**
	 * Disconnect from the current server
	 */
	public void disconnect() {
		messenger.disconnect();
	}
	
	/**
	 * Send a given parcel to the server
	 * @param parcel
	 */
	public void send(Parcel parcel) {
		messenger.sendMessage(parcel);
	}
	
	/**
	 * Listen to all messages on a given topic
	 * Ensure that the given QOS is met
	 * @param topic to listen to
	 * @param qos to enforce
	 */
	public void subscribe(String topic, int qos) {
		messenger.subscribe(topic, qos);
	}
	
	/**
	 * Stop listening to messages on a give topic
	 * @param topic to stop listening to
	 */
	public void unsubscribe(String topic) {
		messenger.unsubscribe(topic);
	}
	
	/**
	 * State of current server connection
	 * @return connection state
	 */
	public boolean isConnected() {
		return messenger.isConnected();
	}
	
	/**
	 * Add a new listener to receive messages collected
	 * by the messenger
	 * @param new listener
	 */
	public void addMessageEventListener(MessageEventListener mel) {
		messenger.addMessageEventListener(mel);
	}
	
	/**
	 * Remove a current listener from receiving messages collected
	 * by the messenger
	 * @param current listener
	 */
	public void removeMessageEventListener(MessageEventListener mel) {
		messenger.removeMessageEventListener(mel);
	}
		
}
