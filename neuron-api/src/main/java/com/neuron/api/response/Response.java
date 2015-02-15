package com.neuron.api.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.neuron.api.model.Payload;

/**
 * Wraps a response object
 * @author JC
 *
 */
public class Response {

	private Payload payload;
	private ArrayList<String> protocols;
	private ArrayList<String> formats;
	private Integer statusCode;
	private Map<String, String> headers;
	
	public Response(Payload payload) {
		this.payload = payload;	
		this.protocols = new ArrayList<String>();
		this.formats = new ArrayList<String>();
		this.headers = new HashMap<String, String>();
	}
	
	public Payload getPayload() {
		return payload;
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
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public String getHeader(final String key) {
		return headers.get(key);
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
}
