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
	
	public Device fromBson(DBObject obj) {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(obj);
		//HashMap map = (HashMap) obj.toMap();
		Device device = new Device();
		device.setId((Integer) obj.get("id"));
		device.setManufacurer((String) obj.get("manufacturer"));
		device.setModel((String) obj.get("model"));
		double lat = (Double)((DBObject)obj.get("geo")).get("latitude");
		double lon = (Double)((DBObject)obj.get("geo")).get("longitude");
		device.setGeo(lon, lat);
		BasicDBList dbSensors = (BasicDBList) obj.get("sensors");
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
		device.setUri((String) obj.get("uri"));
		return device;
	}

	private int id;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("model")
	private String model;
	@JsonProperty("uri")
	private String uri;
	@JsonProperty("geo")
	private GeoPoint geo;
	@JsonProperty("sensors")
	protected ArrayList<Sensor> sensors;
	@JsonProperty("actuators")
	protected ArrayList<Actuator> actuators;

}
