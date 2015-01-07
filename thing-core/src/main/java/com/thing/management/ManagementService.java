package com.thing.management;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.events.DeviceEvent;
import com.thing.api.events.DeviceEventListener;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Device;
import com.thing.messaging.MessagingService;

public class ManagementService implements Runnable, MessageEventListener {

	private static final Logger log = Logger.getLogger( ManagementService.class.getName() );
	
	// Stores messanger id's with device descriptors
	private HashMap<Integer, Device> devices;
	private HashMap<Integer, Long> timestamps;
	private MessagingService msgService;
	private ArrayList<DeviceEventListener> subscribers;
	private static ManagementService instance;
	
	private ManagementService() {
		// hidden
		devices = new HashMap<Integer, Device>();
		timestamps = new HashMap<Integer, Long>();
		msgService = MessagingService.getService();
		subscribers = new ArrayList<DeviceEventListener>();
		
		new Thread(this).start();
	}
	
	public static ManagementService getService() {
		if(instance == null) {
			instance = new ManagementService();
		}
		return instance;
	}

	public synchronized void add(int id, Device device) {
		log.log(Level.INFO, "Adding an device to registry");
		devices.put(id, device);
		String topic = "device/"+id+"/ping/response";
		msgService.subscribe(topic, 2, this);
		timestamps.put(id, System.currentTimeMillis() / 1000L);
		this.notifyListeners(id, "ADD");
	}
	
	public synchronized void remove(int id) {
		log.log(Level.INFO, "Removing an adapter to registry");
		devices.remove(id);
		timestamps.remove(id);
		this.notifyListeners(id, "SUB");
	}
	
	public synchronized HashMap<Integer, Device> getDevices() {
		return this.devices;
	}
	
	public void addDeviceListener(DeviceEventListener listener) {
		this.subscribers.add(listener);
	}
	
	public void removeDeviceListener(DeviceEventListener listener) {
		this.subscribers.remove(listener);
	}
	
	private void pingDevice(int id) {
		String message = "PING";
		String topic = "device/"+id+"/ping/request";
		Parcel parcel = ParcelPacker.makeParcel(id, message, topic, "JSON");
		msgService.sendMessage(parcel);
	}

	public void run() {
		
		final int MIN = 60000;
		final int SEC = 1000;
		
		int TIMEOUT = SEC * 10;
		int POLLING_PERIOD = SEC * 10;
		long threshold;
		
		while(true) {
			
			// Check each device timestamps every x seconds
			threshold = (System.currentTimeMillis() - TIMEOUT) / 1000L;
			
		
			/* By adding each id from the registry to a set and removing afterwards
			 * you avoid the read / write conflict associated with removing items
			 * during iteration directly.
			 */
			HashSet<Integer> set = new HashSet<Integer>();			
			
			for(Integer id : devices.keySet()) {
				pingDevice(id);
			}
			
			// delay for response
			try {Thread.sleep(POLLING_PERIOD);} catch (InterruptedException e) {}
			
			for(Integer id : devices.keySet()) {
				if(timestamps.get(id) < threshold) {
					log.log(Level.WARNING, "Device " + id + " timed out");
					set.add(id);
					notifyListeners(id, "SUB");
				}
			}
			for(Integer i : set) {
				devices.remove(i);
			}
		}
	}
	
	public void notifyListeners(int deviceId, String operation) {
		DeviceEvent event = new DeviceEvent(this, deviceId, operation);
		for(DeviceEventListener subscriber : this.subscribers) {
			subscriber.onDevicesChanged(event);
		}
	}

	public void onMessageArrived(MessageEvent event) {
		int id = event.getMessage().getId();
		timestamps.put(id, System.currentTimeMillis() / 1000L);
	}
	
}
