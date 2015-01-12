package com.thing.api.model;


public class Session {
	
	private int id;
	private Long timestamp;
	private String protocol;
	private String format;
	
	public Session(int id, String protocol, String format, Long timestamp) {
		this.id = id;
		this.protocol = protocol;
		this.format = format;
		this.timestamp = timestamp;
	}
	public Session(int id, String protocol, String format) {
		this.id = id;
		this.protocol = protocol;
		this.format = format;
		this.timestamp = System.currentTimeMillis() / 1000L;
	}
	public int getId() {
		return this.id;
	}
	public String getProtocol() {
		return this.protocol;
	}
	public String getFormat() {
		return this.format;
	}
	public Long getTimeStamp() {
		return this.timestamp;
	}
	public void updateTimeStamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}
	public boolean after(long timestamp) {
		return (this.timestamp > timestamp);
	}
	public boolean before(long timestamp) {
		return (this.timestamp < timestamp);
	}
	public String getPingAddress() {
		return "devices/"+id+"/ping";
	}
}
