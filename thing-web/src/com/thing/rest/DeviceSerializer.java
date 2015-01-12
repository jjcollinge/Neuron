package com.thing.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thing.api.model.Device;
import com.thing.api.model.Sensor;

public class DeviceSerializer extends JsonSerializer<Device> {

	@Override
	public void serialize(Device device, JsonGenerator jsonGen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jsonGen.writeStartObject();
		jsonGen.writeNumberField("id", device.getSessionId());
		jsonGen.writeStringField("manufacturer", device.getManufacturer());
		jsonGen.writeStringField("model", device.getModel());
		jsonGen.writeStringField("uri", device.getUri());
		jsonGen.writeStringField("geo", device.getGeo().toString());
		jsonGen.writeStartArray();
		for(Sensor sensor : device.getSensors()) {
			jsonGen.writeStartObject();
		}
	}

}
