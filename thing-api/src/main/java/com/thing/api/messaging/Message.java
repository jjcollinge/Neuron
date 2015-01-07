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

	private int messangerId;
	private String payload;
	private String format;
	
	public Message(int id, String payload, String format) {
		this.messangerId = id;
		this.payload = payload;
	}
	public int getId() {
		return this.messangerId;
	}
	public String getPayload() {
		return this.payload;
	}
	public String getFormat() {
		return this.format;
	}
}
