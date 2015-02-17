package com.neuron.api.core;

import com.neuron.api.model.Session;

public interface SessionDaemon extends Runnable {

	public void addSession(Session session);
	public void removeSession(Integer sessionId);
	public Session getSession(Integer sessionId);
	public void updateTimestamp(Integer sessionId);
	public void setPingTimeout(int seconds);
	public void setPingPollingPeriod(int seconds);
	
	public void stop();
	
}
