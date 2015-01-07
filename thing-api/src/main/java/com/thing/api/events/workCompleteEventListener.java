package com.thing.api.events;

import com.thing.api.components.Worker;



public interface workCompleteEventListener {

	public void onWorkComplete(Worker worker);
	
}
