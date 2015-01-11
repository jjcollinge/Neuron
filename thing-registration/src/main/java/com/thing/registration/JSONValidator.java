package com.thing.registration;

import com.thing.api.components.Validator;


public class JSONValidator implements Validator {

	//private JsonSchema schema;
	
	public JSONValidator() {
	}
	
	public boolean isValid(String source) {
		
		return true;
	}

	public void setSchema(String location) {
		
	}

}
