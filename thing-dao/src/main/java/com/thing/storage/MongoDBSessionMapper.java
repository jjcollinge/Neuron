package com.thing.storage;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Session;

public class MongoDBSessionMapper {

	public DBObject toBson(Session session) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", session.getId());
		map.put("timestamp", session.getTimestamp());
		for(String prop : session.getProperties().keySet()) {
			map.put(prop, session.getProperty(prop));
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(map);
		} catch (JsonGenerationException e) {
			return null;
		} catch (JsonMappingException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		DBObject bson = (DBObject) JSON.parse(json);
		return bson;
	}
	
	public Session fromBson(DBObject obj) {
		int id = (Integer) obj.get("id");
		Session session = new Session(id);
		for(String key : obj.keySet()) {
			session.addProperty(key, obj.get(key));
		}
		return session;
	}
	
}
