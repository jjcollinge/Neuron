package com.thing.api.events;

import com.thing.api.components.Worker;
import com.thing.api.messaging.Parcel;



public interface workCompleteEventListener {

	public void onWorkComplete(Worker worker, Parcel response);
	
}
