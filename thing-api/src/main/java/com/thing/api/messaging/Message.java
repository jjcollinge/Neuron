package com.thing.api.messaging;

/**
 * Name: Message
 * ---------------------------------------------------------------
 * Desc: The Message class is responsible for providing a standard
 * 		 internal message. This should be specialised for different
 * 		 types of messages.
 * 
 * @author jcollinge
 *
 */
public class Message {

	private int messengerId;
	private String payload;
	private String format;
	
	public Message(int id, String payload, String format) {
		this.messengerId = id;
		this.payload = payload;
		this.format = format;
	}
	public int getId() {
		return this.messengerId;
	}
	public String getPayload() {
		return this.payload;
	}
	public String getFormat() {
		return this.format;
	}
}
