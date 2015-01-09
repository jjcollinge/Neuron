package com.thing.model;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class SensorDeserializer implements JsonDeserializer<ActiveSensor> {

	public ActiveSensor deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		String sense = json.getAsJsonObject().get("sense").getAsString();
		String unit = json.getAsJsonObject().get("unit").getAsString();
		String type = json.getAsJsonObject().get("type").getAsString();
		ActiveSensor sensor = new ActiveSensor(sense, unit, type);
		return sensor;
	}

}
