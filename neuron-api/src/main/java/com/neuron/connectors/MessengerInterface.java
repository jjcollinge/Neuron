package com.neuron.connectors;

import com.neuron.api.messaging.Parcel;

public interface MessengerInterface {

	public void connect(String host, int port);
	public void disconnect();		
	public void sendMessage(Parcel parcel);
	public void subscribe(String topic, int qos);
	public void unsubscribe(String topic);
	public boolean isConnected();
	
}
