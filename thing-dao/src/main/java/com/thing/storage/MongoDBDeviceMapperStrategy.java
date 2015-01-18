package com.thing.storage;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Actuator;
import com.thing.api.model.Device;
import com.thing.api.model.ObjectMapperStrategy;
import com.thing.api.model.Sensor;

public class MongoDBDeviceMapperStrategy implements ObjectMapperStrategy<Device, DBObject> {
	
	public DBObject serialize(Device device) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sessionId", device.getSessionId());
		map.put("manufacturer", device.getManufacturer());
		map.put("model", device.getModel());
		map.put("uri", device.getUri());
		if(device.getGeo() != null) {
			Double[] loc = {device.getGeo().getLongitude(), device.getGeo().getLatitude()};
			map.put("loc", loc);
		}
		map.put("sensors", device.getSensors());
		map.put("actuators", device.getActuators());
		
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

	public Device deserialize(DBObject obj) {
		
		Device device = new Device();
		device.setSessionId((Integer) obj.get("sessionId"));
		device.setManufacurer((String) obj.get("manufacturer"));
		device.setModel((String) obj.get("model"));
		device.setUri((String) obj.get("uri"));
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
				String name = (String) dbSensor.get("name");
				String sense = (String) dbSensor.get("sense");
				String unit = (String) dbSensor.get("unit");
				String value = (String) dbSensor.get("value");
				String type = (String) dbSensor.get("type");
				Sensor s = new Sensor();
				s.setId(sensorId);
				s.setName(name);
				s.setSense(sense);
				s.setType(type);
				s.setUnit(unit);
				s.setValue(value);
				device.addSensor(s);
			}
		}
		BasicDBList dbActuators = (BasicDBList) obj.get("actuators");
		if(dbActuators != null) {
			BasicDBObject[] dbActuatorArray = dbActuators.toArray(new BasicDBObject[0]);
			for(DBObject dbActuator : dbActuatorArray) {
				int actuatorId = (Integer) dbActuator.get("id");
				String name = (String) dbActuator.get("name");
				BasicDBList dbOptions = (BasicDBList) dbActuator.get("options");
				String[] dbOptionsArray = dbOptions.toArray(new String[0]);
				Actuator a = new Actuator();
				for(String dbOption : dbOptionsArray) {
					a.addOption(dbOption.toString());
				}
				a.setId(actuatorId);
				a.setName(name);
				device.addActuator(a);
			}
		};
		return device;
	}
	
}
