package com.thing.storage;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.thing.api.model.Device;

public class DataHandler {

	private static final Logger log = Logger.getLogger(DataHandler.class
			.getName());

	private final String DATABASE_SERVER = "localhost";
	private final String DATABASE_NAME = "database";
	private final String DEVICE_COLLECTION = "devices";
	private final String SESSION_COLLECTION = "sessions";

	private static DataHandler instance;
	private MongoClient client;
	private DB database;

	private DataHandler() {
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

	public void insertDevice(Device device) {
		log.log(Level.INFO, "Inserting new document");
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		DeviceMapper mapper = new DeviceMapper();
		DBObject obj = mapper.toBson(device);
		collection.insert(obj);
	}

	public void removeDevice(Device device) {
		log.log(Level.INFO, "Removing document");
		DBCollection collection = database.getCollection(DEVICE_COLLECTION);
		DeviceMapper mapper = new DeviceMapper();
		DBObject obj = mapper.toBson(device);
		collection.remove(obj);
	}

}
