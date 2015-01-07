package com.thing.api.messaging;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;


/**
 * Name: Messenger
 * ---------------------------------------------------------------
 * Desc: The Messenger class is responsible for listening to a server
 * 		 for new data and notifying any listeners of the data event
 * 
 * @author jcollinge
 *
 */
public abstract class Messenger {

	private int id;
	protected CopyOnWriteArrayList<MessageEventListener> listeners;
	private String type; 
	
	public Messenger(String type, int id) {
		this.type = type;
		this.id = id;
		listeners = new CopyOnWriteArrayList<MessageEventListener>();
	}
	public int getId() {
		return this.id;
	}
	
	public abstract void connect(String host, String port);
	
	public abstract void send(Parcel message);
	
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

	//Event firing method.  Called internally by other class methods.
	protected void notifyListeners(MessageEvent messageEvent) {
	
		for (MessageEventListener listener : listeners) {
	    	listener.onMessageArrived(messageEvent);
	    }
	}
	
}
