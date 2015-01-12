package com.thing.storage;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Session;

public class MongoDBSessionMapper {

	public DBObject toBson(Session session) {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(session);
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
		String protocol = (String) obj.get("protocol");
		String format = (String) obj.get("format");
		Session session = new Session(id, protocol, format);
		return session;
	}
	
}
