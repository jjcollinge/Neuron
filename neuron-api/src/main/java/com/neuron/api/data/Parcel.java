package com.neuron.api.data;

public class Parcel {

	private final Message message;
	private final String topic;
	private final int qos;

	// Required fields - the rest can be set using setter methods
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
		private String topic;
		private int qos;
		
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
