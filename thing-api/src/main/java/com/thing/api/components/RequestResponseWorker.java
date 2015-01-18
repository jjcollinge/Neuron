package com.thing.api.components;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thing.api.events.workCompleteEventListener;
import com.thing.api.messaging.Parcel;

public abstract class RequestResponseWorker implements Runnable {

	protected CopyOnWriteArrayList<workCompleteEventListener> workListeners;
	private Parcel response;
	
	public RequestResponseWorker() {
		workListeners = new CopyOnWriteArrayList<workCompleteEventListener>();
	}
	
	/**
	 * Invoke the workers doWork function when this is ran within a thread
	 */
	public void run() {
		doWork();
		fireOnWorkComplete();
	}
	
	/**
	 * Add a listener which will be notified once the doWork method is complete
	 * @param l
	 */
	public void addCompletionEventListener(workCompleteEventListener wcel) {
		this.workListeners.add(wcel);
	}
	
	/**
	 * Remove a current listener
	 * @param wcel
	 */
	public void removeCompletionEventListener(workCompleteEventListener wcel) {
		this.workListeners.remove(wcel);
	}
	
	/**
	 * This will be the main implemented method which will perform a discrete task
	 */
	protected abstract void doWork();

	/**
	 * This will be called when a response has been formed by the worker
	 */
	public void setResponse(Parcel response) {
		this.response = response;
	}
	
	/**
	 * Notify all listeners that the work has been complete an a response is ready
	 */
	private void fireOnWorkComplete() {	
		for(workCompleteEventListener listener : workListeners) {
			listener.onWorkComplete(this, response);
		}	
	}
}
