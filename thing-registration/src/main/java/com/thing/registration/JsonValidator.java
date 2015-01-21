package com.thing.registration;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.thing.api.components.Validator;

public class JsonValidator implements Validator {

	/**
	 * Test the source is valid JSON
	 */
	public boolean isValid(String source) {
		
		boolean valid = false;
	
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory()
		            .createJsonParser(source);
		      while (parser.nextToken() != null) {}
		      valid = true;
		} catch (JsonParseException jpe) {
		      jpe.printStackTrace();
		} catch (IOException ioe) {
		      ioe.printStackTrace();
		}
		
	    return valid;
	}
}
