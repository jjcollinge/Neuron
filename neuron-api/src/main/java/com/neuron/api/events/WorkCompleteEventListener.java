package com.neuron.api.events;

import com.neuron.api.components.RequestResponseWorker;
import com.neuron.api.data.Parcel;

public interface WorkCompleteEventListener {

	public void onWorkComplete(RequestResponseWorker worker, Parcel response);
	
}
