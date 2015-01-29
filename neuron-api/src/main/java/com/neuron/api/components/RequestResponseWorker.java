package com.neuron.api.components;
import java.util.concurrent.CopyOnWriteArrayList;

import com.neuron.api.data.Parcel;
import com.neuron.api.events.WorkCompleteEventListener;

public abstract class RequestResponseWorker implements Runnable {

	protected CopyOnWriteArrayList<WorkCompleteEventListener> workListeners;
	private Parcel response;
	
	public RequestResponseWorker() {
		workListeners = new CopyOnWriteArrayList<WorkCompleteEventListener>();
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
	public void addCompletionEventListener(WorkCompleteEventListener wcel) {
		this.workListeners.add(wcel);
	}
	
	/**
	 * Remove a current listener
	 * @param wcel
	 */
	public void removeCompletionEventListener(WorkCompleteEventListener wcel) {
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
		for(WorkCompleteEventListener listener : workListeners) {
			listener.onWorkComplete(this, response);
		}	
	}
}
