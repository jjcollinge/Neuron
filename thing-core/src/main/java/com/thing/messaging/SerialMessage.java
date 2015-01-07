package com.thing.messaging;

import com.thing.api.messaging.Message;

public class SerialMessage extends Message {

	private String topic;
	
	public SerialMessage(int id, String payload, String topic, String format) {
		super(id, payload, format);
		this.topic = topic;
	}
	public String getTopic() {
		return this.topic;
	}

}
