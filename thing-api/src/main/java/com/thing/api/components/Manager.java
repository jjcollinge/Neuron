package com.thing.api.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.events.workCompleteEventListener;

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
public abstract class Manager implements workCompleteEventListener {

	private static final Logger log = Logger.getLogger( Manager.class.getName() );
	
	protected int MAX_NUMBER_OF_WORKERS;
	private int numberOfWorkers;
	
	public Manager() {	
		
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
	public void onWorkComplete(Worker worker) {
		// work completed by worker, reallocate
		log.log(Level.INFO, "Worker has finished, reallocating...");
		worker.removeCompletionEventListener(this);
		this.numberOfWorkers--;
	}
	
}
