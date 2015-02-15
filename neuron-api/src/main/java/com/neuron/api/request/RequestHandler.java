package com.neuron.api.request;

import java.util.Vector;

import com.neuron.api.events.RequestListener;

/**
 * Super class for request handlers. Will handle queueing and
 * processing new requests.
 * @author JC
 *
 */
public abstract class RequestHandler implements RequestListener, Runnable {
	
	/**
	 * Thread used to handle the request
	 */
	protected Thread processingThread;
	
	/**
	 * Request queue
	 */
	protected Vector<Request> requestQueue = new Vector<Request>();
	
	/**
	 * Number of current request threads
	 */
	protected static int requestCount = 0;
	
	public RequestHandler() {
		processingThread = new Thread(this);
	}
	
	/**
	 * Handle a single request
	 */
	public abstract void handleRequest(Request request);

	/**
	 * Clear the current request queue
	 */
	public void clearRequests() {
		requestQueue.clear();
	}
	
	/**
	 * Add new request to the request queue
	 */
	public synchronized void addRequest(Request request) {
		if(request != null) {
			requestQueue.add(request);
		}
	}
	
	/**
	 * New request event arrived
	 */
	public void onRequest(Request request) {
		handleRequest(request);
	}
	
	/**
	 * Main processing thread
	 */
	public void run() {
		while(true) {
			if(!requestQueue.isEmpty()) {
				Request request = requestQueue.lastElement();
				if(!request.hasExpired()) {
					handleRequest(request);
				}
			}
		}
	}
}
