package com.thing.connectors;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.sessions.ActivityListener;
import com.thing.sessions.SessionManager;
import com.thing.sessions.model.Session;

/**
 * Name: Messenger
 * ---------------------------------------------------------------
 * Desc: The Messenger class is responsible for listening to a server
 * 		 for new data and notifying any listeners of the data event
 * 
 * @author jcollinge
 *
 */
public abstract class Connector {

	// Listeners who subscribe for forwarded messages from this connector
	protected CopyOnWriteArrayList<MessageEventListener> listeners;
	// The type of message this connector deals with
	private String type;
	
	// Methods
	public Connector(String type) {
		this.type = type;
		listeners = new CopyOnWriteArrayList<MessageEventListener>();
		
		// Register to activity monitor
		SessionManager.getInstance().getMonitor().registerConnector(this);
	}
	public abstract void connect(String host, String port);
	public abstract void disconnect();		
	protected abstract void send(Parcel message);
	public abstract void subscribe(String topic, int qos);
	public abstract void unsubscribe(String topic);
	public abstract boolean isConnected();
	public void addMessageEventListener(MessageEventListener l) {
	    this.listeners.add(l);
	}
	public void removeMessageEventListener(MessageEventListener l) {
	    this.listeners.remove(l);
	}
	public String getType() {
		return this.type;
	}
	// This method wraps a specialised send method and is responsible for locking devices
	public synchronized void sendMessage(Parcel parcel) {
		send(parcel);
	}
	//Event firing method.  Called internally by other class methods.
	protected void notifyListeners(MessageEvent messageEvent) {
		
		// Notify listeners
		for (MessageEventListener listener : listeners) {
	    	listener.onMessageArrived(messageEvent);
	    }
	}
}
