package com.thing.registration;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class Worker implements Runnable {

	protected CopyOnWriteArrayList<CompletionEventListener> workListeners;
	
	public Worker() {
		workListeners = new CopyOnWriteArrayList<CompletionEventListener>();
	}
	
	public void addCompletionEventListener(CompletionEventListener l) {
		this.workListeners.add(l);
	}
	
	public void removeCompletionEventListener(CompletionEventListener l) {
		this.workListeners.remove(l);
	}
	
	protected void finishWork() {
		fireOnWorkComplete();
	}
	
	private void fireOnWorkComplete() {
		
		for(CompletionEventListener listener : workListeners) {
			listener.onCompletionEventReceived(this);
		}
		
	}
}
