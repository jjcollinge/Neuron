package com.neuron.app.mocks;

import com.neuron.api.core.Daemon;
import com.neuron.api.model.Session;

public class SessionDaemonMock implements Daemon {

	private static Session lastSession = null;
	
	public void addSession(Session session) {
		lastSession = session;
	}
	
	public void removeSession(Integer sessionId) {
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public Session getLastSession() {
		return lastSession;
	}
	
	public void resetLastSession() {
		lastSession = null;
	}

	public Session getSession(Integer sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateTimestamp(Integer sessionId) {
		// TODO Auto-generated method stub
		
	}

	public void setPingTimeout(int seconds) {
		// TODO Auto-generated method stub
		
	}

	public void setPingPollingPeriod(int seconds) {
		// TODO Auto-generated method stub
		
	}

}
