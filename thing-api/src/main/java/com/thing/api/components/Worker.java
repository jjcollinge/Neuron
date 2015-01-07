package com.thing.api.components;
import java.util.concurrent.CopyOnWriteArrayList;
import com.thing.api.events.workCompleteEventListener;

/**
 * Name: Worker
 * ---------------------------------------------------------------
 * Desc: The Worker class is responsible for performing a single
 * 		 routine before being disposed. Once the work is completed
 * 		 it will inform any listeners and then be disposed of.
 * 
 * @author jcollinge
 *
 */
public abstract class Worker implements Runnable {

	protected CopyOnWriteArrayList<workCompleteEventListener> workListeners;
	
	public Worker() {
		workListeners = new CopyOnWriteArrayList<workCompleteEventListener>();
	}
	public void addCompletionEventListener(workCompleteEventListener l) {
		this.workListeners.add(l);
	}
	public void removeCompletionEventListener(workCompleteEventListener l) {
		this.workListeners.remove(l);
	}
	protected abstract void doWork();
	
	protected void finishWork() {
		fireOnWorkComplete();
	}
	private void fireOnWorkComplete() {
		
		for(workCompleteEventListener listener : workListeners) {
			listener.onWorkComplete(this);
		}	
	}
}
