package com.thing.connectors;

import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;

public class Connector {

	private Messenger messenger;
	
	public void setMessenger(Messenger messenger) {
		this.messenger = messenger;
	}
	
	public void connect(String host, int port) {
		messenger.connect(host, port);
	}
	
	public void disconnect() {
		messenger.disconnect();
	}
	
	public void send(Parcel parcel) {
		messenger.sendMessage(parcel);
	}
	
	public void subscribe(String topic, int qos) {
		messenger.subscribe(topic, qos);
	}
	
	public void unsubscribe(String topic) {
		messenger.unsubscribe(topic);
	}
	
	public boolean isConnected() {
		return messenger.isConnected();
	}
	
	public void addMessageEventListener(MessageEventListener mel) {
		messenger.addMessageEventListener(mel);
	}
	
	public void removeMessageEventListener(MessageEventListener mel) {
		messenger.removeMessageEventListener(mel);
	}
		
}
