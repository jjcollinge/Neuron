package com.thing.api.events;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class MessageEventProducer {

	protected CopyOnWriteArrayList<MessageEventListener> listeners;

	public MessageEventProducer() {
		listeners = new CopyOnWriteArrayList<MessageEventListener>();
	}

	public void addMessageEventListener(MessageEventListener l) {
		this.listeners.add(l);
	}

	public void removeMessageEventListener(MessageEventListener l) {
		this.listeners.remove(l);
	}

	protected void notifyListeners(MessageEvent messageEvent) {

		for (MessageEventListener listener : listeners) {
			listener.onMessageArrived(messageEvent);
		}
	}
}
