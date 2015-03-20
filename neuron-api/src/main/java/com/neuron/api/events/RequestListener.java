package com.neuron.api.events;

import com.neuron.api.request.Request;

/**
 * An interface for classes whom wish to listen to request events
 * @author JC
 *
 */
public interface RequestListener {

	/**
	 * Event listener for request event
	 * @param request
	 */
	public void onRequest(Request request);
	
}
