package com.thing.messaging;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Messanger {

	private int id;
	
	protected CopyOnWriteArrayList<MessageEventListener> listeners;
	private String type; 
	
	public Messanger(String type, int id) {
		this.type = type;
		this.id = id;
		listeners = new CopyOnWriteArrayList<MessageEventListener>();
	}

	public int getId() {
		return this.id;
	}
	
	public abstract void connect(String host, String port);
	
	public abstract void send(MessagePayload message);
	
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
	protected void fireChangeEvent(MessageEvent msg) {
	MessageEvent event = msg;
	
		for (MessageEventListener listener : listeners) {
	    	listener.messageEventReceived(event);
	    }
	}
	
}
