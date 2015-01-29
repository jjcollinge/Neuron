package com.neuron.api.components;

import java.util.concurrent.CopyOnWriteArrayList;

import com.neuron.api.data.Parcel;
import com.neuron.api.events.WorkCompleteEventListener;

/**
 * The RequestResponseWorker will perform a single procedure
 * or sequence of procedures and the notify any listeners 
 * when it has completed. The worker must compile a response
 * for the worker's controller to dispatch.
 * @author JC
 *
 */
public abstract class RequestResponseWorker implements Runnable {

	// Thread safe list
	protected CopyOnWriteArrayList<WorkCompleteEventListener> workListeners;
	private Parcel response;

	/**
	 * Initialise the listeners collection.
	 */
	public RequestResponseWorker() {
		workListeners = new CopyOnWriteArrayList<WorkCompleteEventListener>();
	}

	/**
	 * The worker will be started within a thread so this method defines
	 * the main sequence of events. Do the required task and then notify
	 * the listenrs when it is complete
	 */
	public void run() {
		doWork();
		fireOnWorkComplete();
	}

	/**
	 * Add a listener which will be notified once the doWork method is complete
	 * 
	 * @param a new listener for the work complete event
	 */
	public void addCompletionEventListener(WorkCompleteEventListener wcel) {
		this.workListeners.add(wcel);
	}

	/**
	 * Remove a current listener
	 * 
	 * @param a current listener for the work complete event
	 */
	public void removeCompletionEventListener(WorkCompleteEventListener wcel) {
		this.workListeners.remove(wcel);
	}

	/**
	 * This will be the main implemented method which will perform a discrete
	 * task or a sequence of discrete tasks
	 */
	protected abstract void doWork();

	/**
	 * This will be called when a response has been formed by the worker
	 */
	public void setResponse(Parcel response) {
		this.response = response;
	}

	/**
	 * Notify all listeners that the work has been complete an a response is
	 * ready
	 */
	private void fireOnWorkComplete() {
		for (WorkCompleteEventListener listener : workListeners) {
			listener.onWorkComplete(this, response);
		}
	}
}
