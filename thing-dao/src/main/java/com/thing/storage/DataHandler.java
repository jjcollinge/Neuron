package com.thing.storage;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.thing.api.events.DeviceEvent;
import com.thing.api.events.DeviceEventListener;
import com.thing.api.model.Device;

public class DataHandler {

	private static final Logger log = Logger.getLogger(DataHandler.class
			.getName());

	private final String DATABASE_SERVER = "localhost";
	private final String DATABASE_NAME = "database";
	private final String DEVICE_COLLECTION = "devices";

	private static DataHandler instance;
	private MongoClient client;
	private DB database;

	private ArrayList<DeviceEventListener> listeners;

	private DataHandler() {
		listeners = new ArrayList<DeviceEventListener>();
		try {
			MongoClient client = new MongoClient(DATABASE_SERVER);
			database = client.getDB(DATABASE_NAME);
		} catch (UnknownHostException e) {
			log.log(Level.INFO, "Failed to connect to database");
		}
	}

	public static DataHandler getInstance() {
		if (instance == null) {
			instance = new DataHandler();
		}
		return instance;
	}

	public void clearDevices() {
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		WriteResult result = collection.remove(new BasicDBObject());
		if (result.getN() > 0) {
			log.log(Level.INFO, "Cleared " + result.getN() + " documents");
		} else {
			log.log(Level.INFO, "No documents to clear");
		}
	}

	public void insertDevice(Device device) {
		log.log(Level.INFO, "Inserting new document");
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		DeviceMapper mapper = new DeviceMapper();
		DBObject obj = mapper.toBson(device);
		if (obj != null) {
			WriteResult result = collection.insert(obj);
			log.log(Level.INFO, "Inserted new document");
			notifyListeners(new DeviceEvent(this, device.getId(), "ADD"));
		}
	}

	public void removeDevice(Device device) {
		log.log(Level.INFO, "Removing document");
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		DeviceMapper mapper = new DeviceMapper();
		DBObject obj = mapper.toBson(device);
		if (obj != null) {
			WriteResult result = collection.remove(obj);
			if (result.getN() > 0) {
				log.log(Level.INFO, "Removed document");
				notifyListeners(new DeviceEvent(this, device.getId(), "SUB"));
			} else {
				log.log(Level.INFO, "Couldn't remove document");
			}
		} else {
			log.log(Level.INFO, "Couldn't find document to remove");
		}
	}

	public Device getDevice(int deviceId) {
		log.log(Level.INFO, "Searching for document");
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		BasicDBObject query = new BasicDBObject("id", deviceId);
		DBObject response = collection.findOne(query);
		Device device = null;
		if (response != null) {
			DeviceMapper mapper = new DeviceMapper();
			device = mapper.fromBson(response);
			log.log(Level.INFO, "Returning found document");
		} else {
			log.log(Level.INFO, "No document found");
		}
		return device;
	}

	public ArrayList<Device> getDevices() {
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		DBCursor cursor = collection.find();
		ArrayList<Device> devices = new ArrayList<Device>();
		DeviceMapper mapper = new DeviceMapper();
		while (cursor.hasNext()) {
			Device device = mapper.fromBson(cursor.next());
			devices.add(device);
		}
		return devices;
	}

	public void addDeviceEventListener(DeviceEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeDeviceEventListener(DeviceEventListener listener) {
		this.listeners.remove(listener);
	}

	public void notifyListeners(DeviceEvent event) {
		for (DeviceEventListener listener : listeners) {
			listener.onDevicesChanged(event);
		}
	}

}
