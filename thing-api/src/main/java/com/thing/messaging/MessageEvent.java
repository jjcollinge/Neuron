package com.thing.messaging;
import java.util.EventObject;


public class MessageEvent extends EventObject {

	private Message msg;
	
	public MessageEvent(Message m) {
		super(m);
		this.msg = m;
	}
	
	public Message getMessage() {
		return this.msg;
	}

}
