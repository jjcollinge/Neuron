package com.thing.messaging;

public class MessagePayload {

	private String topic;
	private String data;
	private int qos;
	private String protocol;
	private String format;
	
	public MessagePayload() {
		
	}
	public MessagePayload(String topic, String payload, int qos) {
		
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setQos(int qos) {
		this.qos = qos;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getTopic() {
		return this.topic;
	}
	public String getData() {
		return this.data;
	}
	public int getQos() {
		return this.qos;
	}
	public String getProtocol() {
		return this.protocol;
	}
	public String getFormat() {
		return this.format;
	}
	
}
