package com.thing.registration;

import com.thing.api.components.Validator;
import com.thing.api.model.Device;


public class RegistrationValidator {

	private Validator validator;
	
	public RegistrationValidator() {
		this.validator = new JSONValidator();
	}
	
	public void setSchemaLocation(String location) {
		this.validator.setSchema(location);
	}
	
	public boolean isValid(String source) {
		return this.validator.isValid(source);
	}
}
