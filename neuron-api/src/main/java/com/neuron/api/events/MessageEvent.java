package com.neuron.api.events;

import java.util.EventObject;

import com.neuron.api.data.Message;

public class MessageEvent extends EventObject {

	private Message msg;	// required
	private int sessionId;	// optional

	public MessageEvent(Object source, Message m) {
		super(source);
		this.msg = m;
		this.sessionId = -1;
	}
	
	public MessageEvent(Object source, Message m, int sessionId) {
		super(source);
		this.msg = m;
		this.sessionId = sessionId;
	}

	public Message getMessage() {
		return this.msg;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}

}
