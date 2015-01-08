package com.thing.registration;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thing.api.components.Validator;
import com.thing.api.model.Device;



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
