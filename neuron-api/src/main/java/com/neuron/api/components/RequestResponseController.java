package com.neuron.api.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.data.Message;
import com.neuron.api.data.Parcel;
import com.neuron.api.events.WorkCompleteEventListener;

/**
 * Provides a super class for any controller whom needs
 * to handle a request and response type of interaction.
 * This controller will handle allocating new workers to
 * deal with each request and free them upon completion.
 * The number of available workers can also be throttled
 * to stop a large number of requests causing the system
 * to fall over (DDOS protection). Any subsequent requests
 * will be dropped by the controller and must be resent at
 * a time when the controller is under less load.
 * @author JC
 *
 */
public abstract class RequestResponseController implements
		WorkCompleteEventListener {

	private static final Logger log = Logger
			.getLogger(RequestResponseController.class.getName());

	private int maxWorkers;
	private int numWorkers;

	/**
	 * Initialises the max number of works permitted
	 */
	public RequestResponseController() {
		maxWorkers = 20;
	}

	/**
	 * Set the maximum number of workers to allocate at once to handling
	 * registration requests
	 * 
	 * @param max Maximum number of workers
	 */
	public void setMaximumNumberOfWorkers(int max) {
		maxWorkers = max;
	}

	/**
	 * Returns the current number of workers
	 * 
	 * @return int The current number of workers allocated
	 */
	@SuppressWarnings("unused")
	private synchronized int getNumberOfWorkers() {
		return numWorkers;
	}

	/**
	 * Returns the maximum number of workers
	 * 
	 * @return the maximum number of workers permitted
	 */
	@SuppressWarnings("unused")
	private synchronized int getMaxNumberOfWorkers() {
		return maxWorkers;
	}

	/**
	 * Check if worker is available and if so register them
	 * 
	 * @param worker The worker to register
	 * @return boolean If the worker was successfully registered
	 */
	private synchronized boolean registerWorker(RequestResponseWorker worker) {
		if (this.numWorkers < this.maxWorkers) {
			worker.addCompletionEventListener(this);
			return true;
		}
		return false;
	}

	/**
	 * Checks the availability of workers. If available the worker
	 * will be registered and their work will be started in a new
	 * thread. If the worker cannot be registered the request will
	 * be dropped.
	 * 
	 * @param worker The worker whom will do the work
	 */
	public synchronized void doWork(RequestResponseWorker worker) {
		if (registerWorker(worker)) {
			log.log(Level.INFO, "Successfully registered worker number "
					+ numWorkers);
			this.numWorkers++;
			new Thread(worker).start();
		} else {
			log.log(Level.INFO,
					"Dropped worker request, maximum number of workers ("
							+ this.maxWorkers + ") has been reached");
		}
	}

	/**
	 * Handle the initial request in the implementation class.
	 * Resulting in a call to the doWork method
	 * @param req The client request
	 */
	public abstract void handleRequest(Message req);

	/**
	 * Handle the dispatch of a response to the same device
	 * whom made the request.
	 * @param res Response to dispatch to client
	 */
	public abstract void handleResponse(Parcel res);

	/**
	 * Called when a worker has completed their current task
	 * @param worker Worker whom has completed the work
	 * @param parcel The response to send back to the client
	 */
	public void onWorkComplete(RequestResponseWorker worker, Parcel parcel) {
		// work completed by worker, reallocate
		log.log(Level.INFO, "Worker has finished, reallocating...");
		handleResponse(parcel);
		worker.removeCompletionEventListener(this);
		this.numWorkers--;
	}

}
