package com.neuron.api.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class Deserializer {

	private static final Logger log = Logger.getLogger(Deserializer.class
			.getName());
	
	/**
	 * Convert string data to POJO
	 * @param format
	 * @param data
	 * @return
	 */
	public static Map<String, String> deserialize(String format, String data) {

		Map<String, String> map = new HashMap<String, String>();
		if (format.equalsIgnoreCase("json")) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				map = mapper.readValue(data,
						new TypeReference<HashMap<String, String>>() {
						});
			} catch (JsonParseException e) {
				log.log(Level.WARNING, "Failed to parse JSON");
			} catch (JsonMappingException e) {
				log.log(Level.WARNING, "Failed to parse JSON, mapping exception");
			} catch (IOException e) {
				log.log(Level.WARNING, "Failed to parse JSON");
			}
		}

		return map;
	}
	
	/**
	 * Try all supported formats
	 * @param data
	 * @return
	 */
	public static Map<String, String> deserialize(String data) {
		
		Map<String, String> map = new HashMap<String, String>();
		map = deserialize("json", data);
		return map;
	}

}
