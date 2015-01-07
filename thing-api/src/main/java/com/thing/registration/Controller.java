package com.thing.registration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements CompletionEventListener {

	private static final Logger log = Logger.getLogger( Controller.class.getName() );
	
	private int MAX_NUMBER_OF_WORKERS = 0;
	private int numberOfWorkers;
	
	public Controller(int max) {
		this.MAX_NUMBER_OF_WORKERS = max;
		this.numberOfWorkers = 0;	
	}
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
	
	public void onCompletionEventReceived(Worker worker) {
		// work completed by worker, reallocate
		log.log(Level.INFO, "Worker has finished, reallocating...");
		worker.removeCompletionEventListener(this);
		this.numberOfWorkers--;
	}
	
}
