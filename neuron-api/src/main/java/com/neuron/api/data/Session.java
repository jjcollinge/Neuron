package com.neuron.api.data;

import com.neuron.api.components.IdGenerator;

public class Session {

	private int id;
	private Long timestamp;
	private Context context;

	public Session() {
		id = IdGenerator.generateId();
		timestamp = System.currentTimeMillis() / 1000L;
	}

	public Session(Integer id) {
		this.id = id;
		timestamp = System.currentTimeMillis() / 1000L;
	}

	public int getId() {
		return this.id;
	}

	public Long getTimestamp() {
		return this.timestamp;
	}

	public void updateTimestamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}

	public boolean isOlder(Long sourceTime) {
		return this.timestamp < sourceTime;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return this.context;
	}
}
