package com.neuron.api.events;

import java.util.concurrent.CopyOnWriteArrayList;

import com.neuron.api.request.Request;

/**
 * In case any requests need forwarding onto another location
 * this class allows you to re-route requests
 * @author JC
 *
 */
public abstract class RequestEventProducer {

	protected CopyOnWriteArrayList<RequestListener> listeners;

	public RequestEventProducer() {
		listeners = new CopyOnWriteArrayList<RequestListener>();
	}

	/**
	 * Add a request event listener
	 * @param l
	 */
	public void addRequestListener(RequestListener l) {
		this.listeners.add(l);
	}

	/**
	 * Remove a request event listener
	 * @param l
	 */
	public void removeRequestListener(RequestListener l) {
		this.listeners.remove(l);
	}

	/**
	 * Notify listener of a request event
	 * @param request
	 */
	protected void notifyListeners(Request request) {

		for (RequestListener listener : listeners) {
			listener.onRequest(request);
		}
	}
}
