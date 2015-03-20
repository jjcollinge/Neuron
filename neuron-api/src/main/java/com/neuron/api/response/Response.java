package com.neuron.api.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.neuron.api.model.Payload;

/**
 * Wraps a response to enable multiple format
 * and protocol support. Any additional headers
 * required by a protocol can be set in the 
 * headers map. If a response should be sent to
 * multiple different servers in multiple formats
 * then they should all be added to the same response
 * and the multicast will be handled by the adapter.
 * @author JC
 *
 */
public class Response {

	private Payload payload;
	private ArrayList<String> protocols;
	private ArrayList<String> formats;
	private Integer statusCode;
	private Map<String, String> headers;
	
	/**
	 * Ctor
	 * @param payload
	 */
	public Response(Payload payload) {
		this.payload = payload;	
		this.protocols = new ArrayList<String>();
		this.formats = new ArrayList<String>();
		this.headers = new HashMap<String, String>();
	}
	
	/**
	 * Get the payload
	 * @return
	 */
	public Payload getPayload() {
		return payload;
	}
	
	/**
	 * Add a protocol to send the response on
	 * @param protocol
	 */
	public void addProtocol(String protocol) {
		this.protocols.add(protocol);
	}
	
	/**
	 * Add a format to send the response in
	 * @param format
	 */
	public void addFormat(String format) {
		this.formats.add(format);
	}
	
	/**
	 * Get all protocols
	 * @return
	 */
	public ArrayList<String> getProtocols() {
		return this.protocols;
	}
	
	/**
	 * Get all formats
	 * @return
	 */
	public ArrayList<String> getFormats() {
		return this.formats;
	}
	
	/**
	 * Set the response status code
	 * @param code
	 */
	public void setStatusCode(Integer code) {
		this.statusCode = code;
	}
	
	/**
	 * Get the response status code
	 * @return
	 */
	public Integer getStatusCode() {
		return this.statusCode;
	}
	
	/**
	 * Add additional header data
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	/**
	 * Get a header
	 * @param key
	 * @return
	 */
	public String getHeader(final String key) {
		return headers.get(key);
	}
	
	/**
	 * Get all headers
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
}
