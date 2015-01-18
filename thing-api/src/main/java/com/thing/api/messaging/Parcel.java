package com.thing.api.messaging;

/**
 * Name: Parcel
 * ---------------------------------------------------------------
 * Desc: The Parcel class provides all the information needed to
 * 		 send a message to a device. 
 * 
 * @author jcollinge
 *
 */
public class Parcel {

	private String topic;
	private Message message;
	private int qos;
	
	// Required fields - the rest can be set using setter methods
	public Parcel(Message message, String topic) {
		this.message = message;
		this.topic = topic;
	}
	public void setQos(int qos) {
		this.qos = qos;
	}
	public String getTopic() {
		return this.topic;
	}
	public Message getMessage() {
		return this.message;
	}
	public int getQos() {
		return this.qos;
	}
}
