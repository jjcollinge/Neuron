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
import com.thing.api.model.Session;
import com.thing.api.model.SessionDAO;
import com.thing.api.model.SessionMapper;

public class MongoDBSessionDAO implements SessionDAO {

	private static final Logger log = Logger.getLogger(MongoDBDeviceDAO.class
			.getName());
	
	private static String DATABASE_HOST = "localhost";
	private static String DATABASE_NAME = "database";
	private static String DATABASE_COLLECTION = "sessions";

	private SessionMapper<DBObject> mapper;
	
	private MongoClient client;
	private DB sessionDatabase;
	private DBCollection sessions;

	public MongoDBSessionDAO() {
		
		// initialise an object mapper
		mapper = new SessionMapper<DBObject>();
		mapper.setMapperStrategy(new MongoDBSessionMapperStrategy());
		
		// initialise database client
		try {
			client = new MongoClient(DATABASE_HOST);
			sessionDatabase = client.getDB(DATABASE_NAME);
			sessions = sessionDatabase.getCollection(DATABASE_COLLECTION);
			log.log(Level.INFO, "Setup up DAO");
		} catch (UnknownHostException e) {
			log.log(Level.INFO, "Failed to connect to database");
		}
	}
	
	public void initialise(String dbhost, String dbname) {
		DATABASE_HOST = dbhost;
		DATABASE_NAME = dbname;
	}
	
	public void insert(Session session) {
		BasicDBObject doc = (BasicDBObject) mapper.serialize(session);
		sessions.insert(doc);
		log.log(Level.INFO, "Inserted session for device " + session.getId() + " to the database.");
	}
	
	public void setCollection(String collectionName) {
		sessions = sessionDatabase.getCollection(collectionName);
	}

	public boolean remove(Integer key) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("id", key);
		DBObject result = sessions.findOne(doc);
		if(result != null) {
			sessions.remove(result);
			log.log(Level.INFO, "Removed session for device " + key + " from the database.");
			return true;
		} else {
			log.log(Level.INFO, "Cannot delete session as it is not in the collection");
			return false;
		}
	}

	public boolean update(Integer key, String field, Object value) {
		BasicDBObject doc = new BasicDBObject();
		doc.append("$set", new BasicDBObject().append(field, value));
		
		BasicDBObject query = new BasicDBObject().append("id", key).append(field, new BasicDBObject("$exists", true));
		WriteResult result = sessions.update(query, doc);
		if(result.getN() > 0) {
			log.log(Level.INFO, "Successfully updated " + result.getN() + " documents");
			return true;
		} else {
			log.log(Level.INFO, "Couldn't update document");
			return false;
		}
	}

	public Session get(Integer key) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("id", key);
		DBObject result = sessions.findOne(doc);
		if(result != null) {
			Session session = mapper.deserialize(result);
			return session;
		} else {
			log.log(Level.INFO, "Session with given id doesn't exist in collection");
			return null;
		}
	}

	public List<Session> findByTimestamp(long timestamp) {
		BasicDBObject query = new BasicDBObject();
		query.put("timestamp", timestamp);
		return findByQuery(query);
	}

	public List<Session> findByProtocol(String protocol) {
		BasicDBObject query = new BasicDBObject();
		query.put("protocol", protocol);
		return findByQuery(query);
	}

	public List<Session> findByFormat(String format) {
		BasicDBObject query = new BasicDBObject();
		query.put("format", format);
		return findByQuery(query);
	}
	
	public List<Session> findByQuery(DBObject query) {
		List<Session> matchingSessions = new ArrayList<Session>();
		DBCursor cursor = sessions.find(query);
		while(cursor.hasNext()) {
			Session session = mapper.deserialize(cursor.next());
			matchingSessions.add(session);
		}
		return matchingSessions;
	}
	
	public void clear() {
		WriteResult result = sessions.remove(new BasicDBObject());
		if (result.getN() > 0) {
			log.log(Level.INFO, "Cleared " + result.getN() + " documents");
		} else {
			log.log(Level.INFO, "No documents to clear");
		}
	}

	public List<Session> find(String field, String value) {
		BasicDBObject query = new BasicDBObject().append(field, value);
		return findByQuery(query);
	}

	public List<Session> getAll() {
		return findByQuery(new BasicDBObject());
	}
}
