package com.thing.storage;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Device;

public class DeviceMapper {

	public DBObject toBson(Device device) {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(device);
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

}
