package com.thing.api.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.events.workCompleteEventListener;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Parcel;

/**
 * Name: Manager
 * ---------------------------------------------------------------
 * Desc: The Manager class is an abstract class which handles
 * 		 the delegation of tasks to workers. The Manager is 
 * 		 responsible for throttling the number of workers working
 * 		 at any one time. 
 * 
 * @author jcollinge
 *
 */
public abstract class RequestResponseController implements workCompleteEventListener {

	private static final Logger log = Logger.getLogger( RequestResponseController.class.getName() );
	
	protected int MAX_NUMBER_OF_WORKERS;
	private int numberOfWorkers;
	
	public RequestResponseController() {	
		
	}
	
	// Clients must implement this method to set the max number of workers
	protected abstract void initialise();
	
	@SuppressWarnings("unused")
	private synchronized int getNumberOfWorkers() {
		return numberOfWorkers;
	}
	@SuppressWarnings("unused")
	private synchronized int getMaxNumberOfWorkers() {
		return MAX_NUMBER_OF_WORKERS;
	}
	private synchronized boolean registerWorker(Worker worker) {
		if(this.numberOfWorkers < this.MAX_NUMBER_OF_WORKERS) {
			worker.addCompletionEventListener(this);
			return true;
		}
		return false;
	}
	public synchronized void doWork(Worker worker) {
		if(registerWorker(worker)) {
			log.log(Level.INFO, "Successfully registered worker number " + numberOfWorkers);
			this.numberOfWorkers++;
			new Thread(worker).start();
		} else {
			log.log(Level.INFO, "Dropped worker request, maximum number of workers (" + this.MAX_NUMBER_OF_WORKERS + ") has been reached");
		}
	}
	public abstract void handleRequest(Message req);
	public abstract void handleResponse(Parcel res);
	public void onWorkComplete(Worker worker, Parcel parcel) {
		// work completed by worker, reallocate
		log.log(Level.INFO, "Worker has finished, reallocating...");
		handleResponse(parcel);
		worker.removeCompletionEventListener(this);
		this.numberOfWorkers--;
	}
	
}
