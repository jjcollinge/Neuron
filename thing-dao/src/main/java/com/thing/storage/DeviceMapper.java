package com.thing.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.thing.api.model.Actuator;
import com.thing.api.model.Device;
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
		HashMap map = (HashMap) obj.toMap();
		Device device = new Device();
		device.setId((Integer) map.get("id"));
		device.setManufacurer((String) map.get("manufacturer"));
		device.setModel((String) map.get("model"));
		device.setGps( (ArrayList<Float>) map.get("gps"));
		device.setSensors( (ArrayList<Sensor>) map.get("sensors"));
		device.setActuators( (ArrayList<Actuator>) map.get("actuators"));
		device.setUri((String) map.get("uri"));
		return device;
	}
	

	private int id;
	@JsonProperty("manufacturer")
	private String manufacturer;
	@JsonProperty("model")
	private String model;
	@JsonProperty("uri")
	private String uri;
	@JsonProperty("gps")
	private ArrayList<Float> gps;
	@JsonProperty("sensors")
	protected ArrayList<Sensor> sensors;
	@JsonProperty("actuators")
	protected ArrayList<Actuator> actuators;

}
