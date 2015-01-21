package com.thing.registration;

import org.codehaus.jackson.map.ObjectMapper;

import com.thing.api.components.Validator;
import com.thing.registration.model.Registration;

public class RegistrationFactory {
	
	private Validator validator;
	
	public RegistrationFactory() {
	}
	
	public Registration getRegistration(String format, String request) {
		
		Registration registration = null;
		
		if(format.equalsIgnoreCase("JSON")) {
			validator = new JsonValidator();
			
			// Test if Json if valid
			if(validator.isValid(request)) {
				
				// Map the registration request onto a POJO
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					registration = mapper.readValue(request, Registration.class);
				} catch (Exception e) {
					
				} 
			}
		}
		return registration;
	}
	
}
