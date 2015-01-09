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

	private String payload;
	private String format;
	private String protocol;
	
	public Message(String payload, String format, String protocol) {
		this.payload = payload;
		this.format = format;
		this.protocol = protocol;
	}
	public String getPayload() {
		return this.payload;
	}
	public String getFormat() {
		return this.format;
	}
	public String getProtocol() {
		return this.protocol;
	}
}
