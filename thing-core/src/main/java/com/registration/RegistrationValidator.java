package com.registration;

import com.thing.registration.Validator;


public class RegistrationValidator {

	private Validator validator;
	
	public RegistrationValidator() {
		this.validator = new JSONValidator();
	}
	
	public void setSchemaLocation(String location) {
		this.validator.setSchema(location);
	}
	
	public String isValid(String source) {
		return this.validator.isValid(source);
	}
}
