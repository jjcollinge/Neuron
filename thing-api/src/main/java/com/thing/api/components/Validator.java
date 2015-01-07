package com.thing.api.components;

/**
 * Name: Validator
 * ---------------------------------------------------------------
 * Desc: The Validator interface should be implemented to validate
 * 		 the content of messages to make sure they compliment the
 * 		 set schema.
 * 
 * @author jcollinge
 *
 */
public interface Validator {

	public String isValid(String source);
	public void setSchema(String location);
	
}
