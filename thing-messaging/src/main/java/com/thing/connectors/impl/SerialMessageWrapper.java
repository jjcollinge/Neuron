package com.thing.connectors.impl;

/**
 * Name: SerialMessageWrapper
 * ---------------------------------------------------------------
 * Desc: Needed to strip the additional shell off Serial messages
 * 
 * @author jcollinge
 *
 */
public class SerialMessageWrapper {

	private String topic;
	private String data;

	public String getTopic() {
		return this.topic;
	}
	public String getData() {
		return this.data;
	}

}
