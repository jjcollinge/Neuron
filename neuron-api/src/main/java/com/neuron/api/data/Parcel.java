package com.neuron.api.data;

/**
 * A parcel is a wrapper class for the internal
 * message structure. It is used for out going
 * messages which need additional data in order
 * to successfully be dispatched (i.e. qos, etc).
 * Ideally the parcel contains all the information
 * required to send a message to an external server.
 * @author JC
 *
 */
public class Parcel {

	private final Message message;
	private final String topic;
	private final int qos;

	private Parcel(ParcelBuilder builder) {
		this.message = builder.message;
		this.topic = builder.topic;
		this.qos = builder.qos;
	}

	public Message getMessage() {
		return this.message;
	}
	
	public String getTopic() {
		return this.topic;
	}

	public int getQos() {
		return this.qos;
	}
	
	public static class ParcelBuilder {
		private final Message message;
		// default values
		private String topic = "#";
		private int qos = 2;
		
		public ParcelBuilder(Message message) {
			this.message = message;
		}
		
		public ParcelBuilder topic(String topic) {
			this.topic = topic;
			return this;
		}
		
		public ParcelBuilder qos(int qos) {
			this.qos = qos;
			return this;
		}
		
		public Parcel build() {
			return new Parcel(this);
		}
	}
}
