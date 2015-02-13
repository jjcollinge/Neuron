package com.neuron.api.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

	private Object data;
	private String sourceProtocol;
	private String sourceFormat;
	private Calendar expiration;
	private Map<String, List<String>> headers;
	
	public Request(Object data) {
		this.data = data;
		this.headers = new HashMap<String, List<String>>();
		expiration = new Calendar.Builder().build();
		expiration.setTime(new Date());
		expiration.add(Calendar.HOUR_OF_DAY, 1);
	}
	
	public Request() {
		this.headers = new HashMap<String, List<String>>();
		expiration = new Calendar.Builder().build();
		expiration.setTime(new Date());
		expiration.add(Calendar.HOUR_OF_DAY, 1);
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setProtocol(String protocol) {
		this.sourceProtocol = protocol;
	}
	
	public void setFormat(String format) {
		this.sourceFormat = format;
	}
	
	public String getProtocol() {
		return this.sourceProtocol;
	}
	
	public String getFormat() {
		return this.sourceFormat;
	}
	
	public void addHeader(String key, String...values) {
		ArrayList<String> props = new ArrayList<String>();
		for(String prop : values) {
			props.add(prop);
		}
		headers.put(key, props);
	}
	
	public boolean hasExpired() {
		return expiration.after(new Date());
	}
	
	public ArrayList<String> getHeader(final String key) {
		return (ArrayList<String>) headers.get(key);
	}
	
	public Map<String, List<String>>  getHeaders() {
		return headers;
	}
	
}
