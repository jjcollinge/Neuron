package com.registration;

import com.thing.api.components.Validator;



public class JSONValidator implements Validator {

	//private JsonSchema schema;
	
	public String isValid(String source) {
		return "JSON";
	}

	public void setSchema(String location) {
		
	}

}
