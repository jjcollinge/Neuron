package com.thing.runner;

import java.util.HashMap;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.management.DeviceManager;
import com.thing.model.ActiveDevice;

public class MockClient implements Runnable, MessageEventListener {

	public void run() {
		
		DeviceManager manager  = DeviceManager.getInstance();
		HashMap<Integer, ActiveDevice> devices = manager.getDevices();
		ActiveDevice device = devices.get(0);
		device.bindToSensor(0, 0, this);
		
	}

	public void onMessageArrived(MessageEvent event) {
		System.out.println(event.getMessage().getPayload());
	}

	
	
}
