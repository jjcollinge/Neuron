package com.thing.runner;

import java.util.ArrayList;
import java.util.HashMap;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.management.SessionManager;
import com.thing.messaging.MqttConnector;
import com.thing.model.ActiveDevice;
import com.thing.model.ActiveSensor;
import com.thing.sessions.Session;

public class MockClient implements Runnable, MessageEventListener {

	public void run() {
		
		SessionManager manager  = SessionManager.getInstance();
		HashMap<Integer, Session> sessions = manager.getDevices();
		Session session = sessions.get(0);
		ActiveDevice device = session.getDevice();
		ArrayList<ActiveSensor> sensors = device.getSensors();
		ActiveSensor sensor = sensors.get(0);
		sensor.initialise(new MqttConnector(), session);
		int i = 10;
		while(i > 0) {
			System.out.println("Value returned: " + sensor.get());
			try { Thread.sleep(1000);} catch(InterruptedException e){}
			i--;
		}
		
	}

	public void onMessageArrived(MessageEvent event) {
		System.out.println(event.getMessage().getPayload());
	}

	
	
}
