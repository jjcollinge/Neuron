package com.thing.messaging;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.sessions.Session;


/**
 * Name: Messenger
 * ---------------------------------------------------------------
 * Desc: The Messenger class is responsible for listening to a server
 * 		 for new data and notifying any listeners of the data event
 * 
 * @author jcollinge
 *
 */
public abstract class Connector implements Runnable {

	protected CopyOnWriteArrayList<MessageEventListener> listeners;
	private String type; 
	private LinkedList<HashMap<Parcel, Session>> messageQueue;
	
	public Connector(String type) {
		this.type = type;
		listeners = new CopyOnWriteArrayList<MessageEventListener>();
		messageQueue = new LinkedList<HashMap<Parcel, Session>>();
		new Thread(this).start();
	}
	public abstract void connect(String host, String port);
	
	protected abstract void send(Parcel message);
	
	public void sendMessage(Parcel parcel, Session session) {
		if(session.isLocked()) {
			System.out.println("Session is locked, adding to queue: " + parcel.getMessage().getPayload());
			HashMap<Parcel,Session> entry = new HashMap<Parcel,Session>();
			entry.put(parcel, session);
			messageQueue.addLast(entry);
		} else if(session.shouldLockOnSend()){
			System.out.println("Locking session and sending message now: " + parcel.getMessage().getPayload());
			session.lock();
			send(parcel);
		} else {
			System.out.println("Session is unlocked, sending now: " + parcel.getMessage().getPayload());
			send(parcel);
		}
	}
	
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
	private void checkQueue() {
		for(HashMap<Parcel,Session> entry : messageQueue) {
			for(Parcel key : entry.keySet()) {
				if(!entry.get(key).isLocked()) {
					System.out.println("Session has been unlocked, sending message from queue: " + key.getMessage().getPayload());
					messageQueue.remove(entry);
					send(key);
				}
			}
		}
	}
	public void run() {
		checkQueue();
		try {Thread.sleep(1000);} catch(InterruptedException e) {}
	}
	//Event firing method.  Called internally by other class methods.
	protected void notifyListeners(MessageEvent messageEvent) {
	
		for (MessageEventListener listener : listeners) {
	    	listener.onMessageArrived(messageEvent);
	    }
	}	
}
