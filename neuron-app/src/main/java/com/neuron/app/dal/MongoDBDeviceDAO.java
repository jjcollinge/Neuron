package com.neuron.app.dal;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.data_access.DeviceMapper;
import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;

/**
 * A MongoDB implementation of the DeviceDAO.
 * Provides MongoDB specific implementation
 * details and is dependent on the
 * MongoDBDeviceMapperStrategy for object
 * mapping.
 * @author JC
 *
 */
public class MongoDBDeviceDAO implements DeviceDAO {
	
	private static final Logger log = Logger.getLogger(MongoDBDeviceDAO.class
			.getName());
	
	// Default values
	private static String DATABASE_COLLECTION = "devices";
	private static String DATABASE_HOST = "localhost";
	private static int DATABASE_PORT = 27017;
	private static String DATABASE_NAME = "devices";
	
	private MongoClient client;
	private DB deviceDatabase;
	private DBCollection devices;
	
	private DeviceMapper<DBObject> mapper;
	
	public MongoDBDeviceDAO() {
		
		// initialise an object mapper
		mapper = new DeviceMapper<DBObject>();
		mapper.setMapperStrategy(new MongoDBDeviceMapperStrategy());
		
		// initialise database client
		try {
			client = new MongoClient(DATABASE_HOST);
			deviceDatabase = client.getDB(DATABASE_NAME);
			devices = deviceDatabase.getCollection(DATABASE_COLLECTION);
			devices.ensureIndex(new BasicDBObject("loc", "2d"), "geospacialIdx");
		} catch (UnknownHostException e) {
			log.log(Level.INFO, "Failed to connect to database");
		}
	}
	
	/**
	 * Initialise the static database configuration (i.e. hostname, port, database)
	 */
	public void initialise(String dbhost, int dbport, String dbname) {
		DATABASE_HOST = dbhost;
		DATABASE_PORT = dbport;
		DATABASE_NAME = dbname;
	}

	/**
	 * Close the connection to the server
	 */
	public void finalize() {
		client.close();
	}
	
	public void setCollection(String collectionName) {
		devices = deviceDatabase.getCollection(collectionName);
	}
	
	public void insert(Device device) {
		BasicDBObject doc = (BasicDBObject) mapper.serialize(device);
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
		BasicDBObject doc = new BasicDBObject();
		doc.append("$set", new BasicDBObject().append(field, value));
		
		BasicDBObject query = new BasicDBObject().append("sessionId", key).append(field, new BasicDBObject("$exists", true));
		WriteResult result = devices.update(query, doc);
		if(result.getN() > 0) {
			log.log(Level.INFO, "Successfully updated " + result.getN() + " documents");
			return true;
		} else {
			log.log(Level.INFO, "Couldn't update document");
			return false;
		}
	}

	public Device get(Integer id) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("sessionId", id);
		DBObject result = devices.findOne(doc);
		if(result !=  null) {
			Device device = mapper.deserialize(result);
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
		query.put("sensors.sense", sense);
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
		query.put("actuators.capbability", capbability);
		return findByQuery(query);
	}
	
	public List<Device> findByQuery(BasicDBObject query) {
		List<Device> matchingDevices = new ArrayList<Device>();
		DBCursor cursor = devices.find(query);
		while(cursor.hasNext()) {
			Device device = mapper.deserialize(cursor.next());
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
