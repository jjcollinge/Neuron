package com.neuron.api.components;

/**
 * Provides an interface for any specific
 * implementations. Should validate a string
 * as a particular format.
 * @author JC
 *
 */
public interface Validator {

	/**
	 * Check whether the given string is a of a valid
	 * format.
	 * @param source The source string to validate 
	 * @return boolean If the validation was successful
	 */
	public boolean isValid(String source);
	
	/**
	 * Check whether the given string is of a valid
	 * format and matches a given schema.
	 * @param source The source string to validate
	 * @param schemaFilename The schema to validate against
	 * @return boolean If the validation was successful
	 */
	public boolean isValid(String source, String schemaFilename);
	
}
