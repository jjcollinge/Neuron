package com.thing.runner;

import java.util.HashMap;

import com.thing.api.model.Device;
import com.thing.management.DeviceManager;
import com.thing.model.ActiveDevice;

public class MockClient implements Runnable {

	public void run() {
		
		DeviceManager dm = DeviceManager.getInstance();
		HashMap<Integer, ActiveDevice> devices = dm.getDevices();
		for(Integer i : devices.keySet()) {
			System.out.println("Device id: " + i + ", Device descriptor: " + devices.get(i));
			ActiveDevice device = devices.get(i);
			while(true) {
				System.out.println("Sensor value: " + device.getValueOf(1, 0));
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
			
		}
	}

	
	
}
