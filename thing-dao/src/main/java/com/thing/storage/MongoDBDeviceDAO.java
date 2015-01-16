package com.thing.storage;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.thing.api.model.Device;
import com.thing.api.model.DeviceDAO;
import com.thing.api.model.GeoPoint;

public class MongoDBDeviceDAO implements DeviceDAO {
	
	private static final Logger log = Logger.getLogger(MongoDBDeviceDAO.class
			.getName());
	
	private final String DATABASE_HOST = "localhost";
	private final String DATABASE_NAME = "database";
	private final String DEVICE_COLLECTION = "devices";
	
	private MongoClient client;
	private DB deviceDatabase;
	private DBCollection devices;
	
	public MongoDBDeviceDAO() {
		try {
			client = new MongoClient(DATABASE_HOST);
			deviceDatabase = client.getDB(DATABASE_NAME);
			devices = deviceDatabase.getCollection(DEVICE_COLLECTION);
		} catch (UnknownHostException e) {
			log.log(Level.INFO, "Failed to connect to database");
		}
	}

	public void finalize() {
		client.close();
	}
	
	public void setCollection(String collectionName) {
		devices = deviceDatabase.getCollection(collectionName);
		devices.ensureIndex(new BasicDBObject("loc", "2d"), "geospacialIdx");
	}
	
	public void insert(Device device) {
		MongoDBDeviceMapper mapper = new MongoDBDeviceMapper();
		BasicDBObject doc = (BasicDBObject) mapper.toBson(device);
		log.log(Level.INFO, "document: " + doc);
		devices.insert(doc);
		log.log(Level.INFO, "Inserted device " + device.getSessionId() + " to the database.");
	}

	public boolean remove(Integer id) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("sessionId", id);
		DBObject result = devices.findOne(doc);
		if(result != null) {
			devices.remove(doc);
			log.log(Level.INFO, "Removed device " + id + " from the database.");
			return true;
		} else {
			log.log(Level.INFO, "Cannot delete device as it is not in the collection");
			return false;
		}
	}
	
	public boolean update(Integer key, String field, Object value) {
		//TODO
		return true;
	}

	public Device get(Integer id) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("sessionId", id);
		DBObject result = devices.findOne(doc);
		if(result !=  null) {
			MongoDBDeviceMapper mapper = new MongoDBDeviceMapper();
			Device device = mapper.fromBson(result);
			return device;
		} else {
			log.log(Level.INFO, "Device with given id doesn't exist in collection");
			return null;
		}
	}

	public List<Device> findByGeo(GeoPoint geo, int proximityInMeters) {
		// Rough conversion from miles to degrees
		double proximityInDegrees = (proximityInMeters/1000.0)/111.12;
	
		BasicDBObject query = new BasicDBObject();
		final BasicDBObject filter = new BasicDBObject("$near", new double[] {geo.getLongitude(), geo.getLatitude()});
		filter.put("$maxDistance", proximityInDegrees);
		query.put("loc", filter);
		
		return findByQuery(query);
	}

	public List<Device> findBySensorCapability(String sense) {
		BasicDBObject query = new BasicDBObject();
		query.put("sense", sense);
		return findByQuery(query);
	}

	public List<Device> findByManufacturer(String manufacturer) {
		BasicDBObject query = new BasicDBObject();
		query.put("manufacturer", manufacturer);
		return findByQuery(query);
	}

	public List<Device> findByModel(String model) {
		BasicDBObject query = new BasicDBObject();
		query.put("model", model);
		return findByQuery(query);
	}

	public List<Device> findByActuatorCapability(String capbability) {
		BasicDBObject query = new BasicDBObject();
		query.put("capbability", capbability);
		return findByQuery(query);
	}
	
	public List<Device> findByQuery(BasicDBObject query) {
		List<Device> matchingDevices = new ArrayList<Device>();
		DBCursor cursor = devices.find(query);
		MongoDBDeviceMapper mapper = new MongoDBDeviceMapper();
		while(cursor.hasNext()) {
			Device device = mapper.fromBson(cursor.next());
			matchingDevices.add(device);
		}
		return matchingDevices;
	}
	
	public void clear() {
		WriteResult result = devices.remove(new BasicDBObject());
		if (result.getN() > 0) {
			log.log(Level.INFO, "Cleared " + result.getN() + " documents");
		} else {
			log.log(Level.INFO, "No documents to clear");
		}
	}

	public List<Device> find(String field, String value) {
		BasicDBObject query = new BasicDBObject().append(field, value);
		return findByQuery(query);
	}

	public List<Device> getAll() {
		return findByQuery(new BasicDBObject());
	}
}
