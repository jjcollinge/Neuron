package com.neuron.api.connectors;

import com.neuron.api.events.RequestEventProducer;

/**
 * Bypasses multiple inheritance restrictions by
 * augmenting the Messenger interface with a 
 * MessageEventProducer to produce a clean abstraction
 * for Messenger implementations to implement.
 * @author JC
 *
 */
public abstract class ProtocolAdapter extends RequestEventProducer implements Messenger {
	// Forms super type for all implemented messengers
}
