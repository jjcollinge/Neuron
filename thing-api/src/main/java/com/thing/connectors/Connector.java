package com.thing.connectors;

import com.thing.api.messaging.Parcel;

public interface Connector {

	public void connect(String host, String port);
	public void disconnect();		
	public void sendMessage(Parcel message);
	public void subscribe(String topic, int qos);
	public void unsubscribe(String topic);
	public boolean isConnected();
	
}
