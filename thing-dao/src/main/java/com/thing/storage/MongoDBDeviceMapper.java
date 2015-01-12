package com.thing.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Actuator;
import com.thing.api.model.Device;
import com.thing.api.model.GeoPoint;
import com.thing.api.model.Sensor;

public class MongoDBDeviceMapper {

	public DBObject toBson(Device device) {
		ObjectMapper mapper = new ObjectMapper();
		
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("sessionId", device.getSessionId());
		hashmap.put("manufacturer", device.getManufacturer());
		hashmap.put("model", device.getModel());
		if(device.getGeo() != null) {
			Double[] loc = {device.getGeo().getLongitude(), device.getGeo().getLatitude()};
			hashmap.put("loc", loc);
		}
		hashmap.put("sensors", device.getSensors());
		hashmap.put("actuators", device.getActuators());
		
		String json = "";
		try {
			json = mapper.writeValueAsString(hashmap);
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
	
	public Device fromBson(DBObject obj) {
		Device device = new Device();
		device.setSessionId((Integer) obj.get("sessionId"));
		device.setManufacurer((String) obj.get("manufacturer"));
		device.setModel((String) obj.get("model"));
		if(obj.get("loc") != null) {
			BasicDBList coordinates =  (BasicDBList) obj.get("loc");
			double lon = (Double) coordinates.get(0);
			double lat = (Double) coordinates.get(1);
			device.setGeo(lat, lon);
		}
		BasicDBList dbSensors = (BasicDBList) obj.get("sensors");
		if(dbSensors != null) {
			BasicDBObject[] dbSensorsArray = dbSensors.toArray(new BasicDBObject[0]);
			for(DBObject dbSensor : dbSensorsArray) {
				int sensorId = (Integer) dbSensor.get("id");
				String sense = (String) dbSensor.get("sense");
				String unit = (String) dbSensor.get("unit");
				String value = (String) dbSensor.get("value");
				String type = (String) dbSensor.get("type");
				Sensor s = new Sensor();
				s.setId(sensorId);
				s.setSense(sense);
				s.setType(type);
				s.setUnit(unit);
				s.setValue(value);
				device.addSensor(s);
			}
		}
		device.setUri((String) obj.get("uri"));
		return device;
	}
	
}
