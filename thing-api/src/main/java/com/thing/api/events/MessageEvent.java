package com.thing.api.events;
import java.util.EventObject;

import com.thing.api.messaging.Message;


public class MessageEvent extends EventObject {

	private Message msg;
	
	public MessageEvent(Object source, Message m) {
		super(source);
		this.msg = m;
	}
	
	public Message getMessage() {
		return this.msg;
	}

}
