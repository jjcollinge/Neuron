package com.neuron.api.request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a request on the system. Enables
 * multiple protocols and formats to
 * be supported by the system. Requests 
 * can have an expiration period set
 * which when reached will render the
 * request obsolete.
 * @author JC
 *
 */
public class Request {

	private Object data;
	private String sourceProtocol;
	private String sourceFormat;
	private Calendar expiration;
	private Map<String, List<String>> headers;
	
	/**
	 * Ctor with data
	 * @param data
	 */
	public Request(Object data) {
		this.data = data;
		this.headers = new HashMap<String, List<String>>();
		expiration = new Calendar.Builder().build();
		expiration.setTime(new Date());
		expiration.add(Calendar.HOUR_OF_DAY, 1);
	}
	
	/**
	 * Ctor
	 */
	public Request() {
		this.headers = new HashMap<String, List<String>>();
		expiration = new Calendar.Builder().build();
		expiration.setTime(new Date());
		expiration.add(Calendar.HOUR_OF_DAY, 1);
	}
	
	/**
	 * Set the data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * Get data
	 * @return
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * Set the request protocol
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.sourceProtocol = protocol;
	}
	
	/**
	 * Set the request format
	 * @param format
	 */
	public void setFormat(String format) {
		this.sourceFormat = format;
	}
	
	/**
	 * Get request protocol
	 * @return
	 */
	public String getProtocol() {
		return this.sourceProtocol;
	}
	
	/**
	 * Get request format
	 * @return
	 */
	public String getFormat() {
		return this.sourceFormat;
	}
	
	/**
	 * Add a header
	 * @param key
	 * @param values
	 */
	public void addHeader(String key, String...values) {
		ArrayList<String> props = new ArrayList<String>();
		for(String prop : values) {
			props.add(prop);
		}
		headers.put(key, props);
	}
	
	/**
	 * Boolean check if request has expired
	 * @return
	 */
	public boolean hasExpired() {
		return expiration.after(new Date());
	}
	
	/**
	 * Get a header
	 * @param key
	 * @return
	 */
	public ArrayList<String> getHeader(final String key) {
		return (ArrayList<String>) headers.get(key);
	}
	
	/**
	 * Get all headers
	 * @return
	 */
	public Map<String, List<String>>  getHeaders() {
		return headers;
	}
	
}
