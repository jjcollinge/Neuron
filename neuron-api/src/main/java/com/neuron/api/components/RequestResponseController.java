package com.neuron.api.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.events.WorkCompleteEventListener;

public abstract class RequestResponseController implements
		WorkCompleteEventListener {

	private static final Logger log = Logger
			.getLogger(RequestResponseController.class.getName());

	private int maxNumberOfWorkers;
	private int numberOfWorkers;

	public RequestResponseController() {
		maxNumberOfWorkers = 20;
	}
	
	/**
	 * 	Set the maximum number of workers to allocate at once to
	 *  handling registration requests
	 * @param max
	 */
	public void setMaximumNumberOfWorkers(int max) {
		maxNumberOfWorkers = max;
	}

	/**
	 * Returns the current number of workers
	 * @return
	 */
	@SuppressWarnings("unused")
	private synchronized int getNumberOfWorkers() {
		return numberOfWorkers;
	}

	/**
	 * Returns the maximum number of workers
	 * @return
	 */
	@SuppressWarnings("unused")
	private synchronized int getMaxNumberOfWorkers() {
		return maxNumberOfWorkers;
	}

	/**
	 * Check if worker is available and if so register them
	 * @param worker
	 * @return
	 */
	private synchronized boolean registerWorker(RequestResponseWorker worker) {
		if (this.numberOfWorkers < this.maxNumberOfWorkers) {
			worker.addCompletionEventListener(this);
			return true;
		}
		return false;
	}

	/**
	 * Check registration status and allocate new thread for worker
	 * @param worker
	 */
	public synchronized void doWork(RequestResponseWorker worker) {
		if (registerWorker(worker)) {
			log.log(Level.INFO, "Successfully registered worker number "
					+ numberOfWorkers);
			this.numberOfWorkers++;
			new Thread(worker).start();
		} else {
			log.log(Level.INFO,
					"Dropped worker request, maximum number of workers ("
							+ this.maxNumberOfWorkers + ") has been reached");
		}
	}

	public abstract void handleRequest(Message req);

	public abstract void handleResponse(Parcel res);

	/**
	 * Called when a worker has completed their current task
	 */
	public void onWorkComplete(RequestResponseWorker worker, Parcel parcel) {
		// work completed by worker, reallocate
		log.log(Level.INFO, "Worker has finished, reallocating...");
		handleResponse(parcel);
		worker.removeCompletionEventListener(this);
		this.numberOfWorkers--;
	}

}
