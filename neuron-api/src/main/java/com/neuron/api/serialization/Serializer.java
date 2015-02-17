package com.neuron.api.serialization;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Convert a POJO into one of the supported
 * serialized formats.
 * @author JC
 *
 */
public class Serializer {

	public static String serialize(String format, Object object) {
		
		if(object == null) return null;
		
		String serializedString = null;
		
		if(format.equalsIgnoreCase("json")) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				serializedString = mapper.writeValueAsString(object);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return serializedString;
	}
	
}
