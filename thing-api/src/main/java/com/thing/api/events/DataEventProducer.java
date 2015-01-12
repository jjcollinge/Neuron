package com.thing.api.events;

import java.util.concurrent.CopyOnWriteArrayList;

public class DataEventProducer {
	
	protected CopyOnWriteArrayList<DataEventListener> listeners;

	public DataEventProducer() {
		listeners = new CopyOnWriteArrayList<DataEventListener>();
	}

	public void addDataEventListener(DataEventListener l) {
		this.listeners.add(l);
	}

	public void removeDataEventListener(DataEventListener l) {
		this.listeners.remove(l);
	}

	protected void notifyListeners(DataEvent dataEvent) {

		for (DataEventListener listener : listeners) {
			listener.onDataArrived(dataEvent);
		}
	}
	
}
