package com.thing.messaging;

import com.google.gson.Gson;

public class Message {

	private int messangerId;
	private MessagePayload payload;
	
	public int getId() {
		return this.messangerId;
	}
	public MessagePayload getPayload() {
		return this.payload;
	}
	public void setId(int id) {
		this.messangerId = id;
	}
	public void setMessagePayload(MessagePayload payload) {
		this.payload = payload;
	}
	
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
