package com.neuron.api.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neuron.api.data.Payload;

/**
 * Wraps a response object
 * @author JC
 *
 */
public class Response {

	private String data;
	private ArrayList<String> protocols;
	private ArrayList<String> formats;
	private Integer statusCode;
	private Map<String, List<String>> headers;
	
	public Response(String data) {
		this.data = data;
		
		this.protocols = new ArrayList<String>();
		this.formats = new ArrayList<String>();
		this.headers = new HashMap<String, List<String>>();
	}
	
	public String getData() {
		return data;
	}
	
	public void addProtocol(String protocol) {
		this.protocols.add(protocol);
	}
	
	public void addFormat(String format) {
		this.formats.add(format);
	}
	
	public ArrayList<String> getProtocols() {
		return this.protocols;
	}
	
	public ArrayList<String> getFormats() {
		return this.formats;
	}
	
	public void setStatusCode(Integer code) {
		this.statusCode = code;
	}
	
	public Integer getStatusCode() {
		return this.statusCode;
	}
	
	public void addHeader(String key, String...values) {
		ArrayList<String> props = new ArrayList<String>();
		for(String prop : values) {
			props.add(prop);
		}
		headers.put(key, props);
	}
	
	public ArrayList<String> getHeader(final String key) {
		return (ArrayList<String>) headers.get(key);
	}
	
	public Map<String, List<String>>  getHeaders() {
		return headers;
	}
}
