package com.neuron.app.activities.registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.neuron.api.data_access.ObjectMapperStrategy;

public class JsonRegistrationMapper implements ObjectMapperStrategy<Registration, String> {

	private static final Logger log = Logger.getLogger(JsonRegistrationMapper.class
			.getName());
	
	public String serialize(Registration source) {
		
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		
		try {
			result = mapper.writeValueAsString(source);
		} catch (JsonGenerationException e) {
			log.log(Level.WARNING, "Failed to parse Json: generation exception");
		} catch (JsonMappingException e) {
			log.log(Level.WARNING, "Failed to parse Json: mapping exception");
		} catch (IOException e) {
			log.log(Level.WARNING, "Failed to parse Json: io exception");
		}
		return result;		
	}

	public Registration deserialize(String source) {

		ObjectMapper mapper = new ObjectMapper();
		Registration reg = null;
		
		if(source != null) {
			try {
				reg = mapper.readValue(source, Registration.class);
			} catch (JsonParseException e) {
				log.log(Level.WARNING, "Failed to parse JSON: parse exception", e);
			} catch (JsonMappingException e) {
				log.log(Level.WARNING, "Failed to parse JSON: mapping exception", e);
			} catch (IOException e) {
				log.log(Level.WARNING, "Failed to parse JSON: io exception", e);
			} 
		}
		return reg;
	}


}
