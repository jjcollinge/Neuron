package com.thing.api.events;

import com.thing.api.components.RequestResponseWorker;
import com.thing.api.messaging.Parcel;

public interface workCompleteEventListener {

	public void onWorkComplete(RequestResponseWorker worker, Parcel response);
	
}
