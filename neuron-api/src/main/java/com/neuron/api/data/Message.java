package com.neuron.api.data;

/**
 * An internal message structure. Used to pass data
 * between system components. Contains a payload
 * which can contain nested payload wrapped in a 
 * string. The context holds the information about
 * the environment of the 
 * payload (i.e. format, protocol etc) 
 * @author JC
 *
 */
public class Message {

	private String payload;
	private Context context;
	
	/**
	 * The Message class is responsible for providing a standard
	 * internal message. This should be specialised for different
	 * types of messages.
	 * @param payload
	 * @param protocol
	 * @param format
	 */
	public Message(String payload, String protocol, String format) {
		this.payload = payload;
		this.context = new Context(protocol, format);
	}
	public void attachContext(Context context) {
		this.context = context;
	}
	public String getPayload() {
		return this.payload;
	}
	public String getFormat() {
		return this.context.getFormat();
	}
	public String getProtocol() {
		return this.context.getProtocol();
	}
}