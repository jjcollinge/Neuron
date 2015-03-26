package com.neuron.app.dal;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.neuron.api.data_access.ObjectMapperStrategy;
import com.neuron.api.model.Actuator;
import com.neuron.api.model.Device;
import com.neuron.api.model.Sensor;

public class MongoDBDeviceMapperStrategy implements ObjectMapperStrategy<Device, DBObject> {
	
	/**
	 * Implementation details for serializing a registration into a mongoDB object
	 */
	public DBObject serialize(Device device) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sessionId", device.getSessionId());
		map.put("name", device.getName());
		if(device.getGeo() != null) {
			Double[] loc = {device.getGeo().getLongitude(), device.getGeo().getLatitude()};
			map.put("loc", loc);
		}
		map.put("sensors", device.getSensors());
		map.put("actuators", device.getActuators());
		map.put("tags", device.getTags());
		
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

	/**
	 * Implementation details for deserializing a registration mongoDB into a registration
	 */
	public Device deserialize(DBObject obj) {
		
		Device device = new Device();
		device.setSessionId((Integer) obj.get("sessionId"));
		device.setName((String) obj.get("name"));
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
				String desc = (String) dbSensor.get("desc");
				String sense = (String) dbSensor.get("sense");
				String unit = (String) dbSensor.get("unit");
				DBObject dbTags = (DBObject) dbSensor.get("tags");
				HashMap<String, String> tags = (HashMap<String, String>) dbTags.toMap();
				Sensor s = new Sensor();
				s.setId(sensorId);
				s.setDesc(desc);
				s.setSense(sense);
				s.setUnit(unit);
				s.setTags(tags);
				device.addSensor(s);
			}
		}
		BasicDBList dbActuators = (BasicDBList) obj.get("actuators");
		if(dbActuators != null) {
			BasicDBObject[] dbActuatorArray = dbActuators.toArray(new BasicDBObject[0]);
			for(DBObject dbActuator : dbActuatorArray) {
				int actuatorId = (Integer) dbActuator.get("id");
				String desc = (String) dbActuator.get("desc");
				BasicDBList dbOptions = (BasicDBList) dbActuator.get("options");
				String[] dbOptionsArray = dbOptions.toArray(new String[0]);
				DBObject dbTags = (DBObject) dbActuator.get("tags");
				HashMap<String, String> tags = (HashMap<String, String>) dbTags.toMap();
				Actuator a = new Actuator();
				for(String dbOption : dbOptionsArray) {
					a.addOption(dbOption.toString());
				}
				a.setId(actuatorId);
				a.setDesc(desc);
				a.setTags(tags);
				device.addActuator(a);
			}
		}
		DBObject objTags = (DBObject) obj.get("tags");
		if(objTags != null) {
			HashMap<String, String> tags = (HashMap<String, String>) objTags.toMap();
			device.setTags(tags);
		}
		return device;
	}
	
}
