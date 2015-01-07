package com.thing.messaging;

import com.thing.api.events.MessageEvent;
import com.thing.api.messaging.Message;

public class RoutedMessageEvent extends MessageEvent {

	private String returnTopic;
	
	public RoutedMessageEvent(Object source, Message m, String returnTopic) {
		super(source, m);
		this.returnTopic = returnTopic;
	}
	public String getTopic() {
		return this.returnTopic;
	}

}
