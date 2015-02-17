package com.neuron.app.validation;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.neuron.api.validation.Validator;

/**
 * Validates a string for Json syntax or against
 * a provided Json schema.
 * @author JC
 *
 */
public class JsonValidator implements Validator {

	private static final Logger log = Logger.getLogger(JsonValidator.class
			.getName());

	
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
		      log.log(Level.INFO, "Malformed Json, not valid");
		      valid = false;
		} catch (IOException ioe) {
			log.log(Level.INFO, "IO exception, not valid");
			valid = false;
		}
		
	    return valid;
	}

	/**
	 * Test source is valid JSON and validate against the schema
	 */
	public boolean isValid(String source, String schemaFilename) {
		// TODO Auto-generated method stub
		return false;
	}
}
