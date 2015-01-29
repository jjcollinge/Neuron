package com.neuron.api.connectors;

import com.neuron.api.events.MessageEventProducer;

/**
 * Bypasses multiple inheritance restrictions by
 * augmenting the Messenger interface with a 
 * MessageEventProducer to produce a clean abstraction
 * for Messenger implementations to implement.
 * @author JC
 *
 */
public abstract class Messenger extends MessageEventProducer implements MessengerInterface {
	// Forms super type for all implemented messengers
}
