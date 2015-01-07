package com.thing.registration;

public interface Validator {

	public String isValid(String source);
	public void setSchema(String location);
	
}
