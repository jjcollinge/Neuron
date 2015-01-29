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
	 * @param source 
	 * @return if it is valid source
	 */
	public boolean isValid(String source);
	
	/**
	 * Check whether the given string is of a valid
	 * format and matches a given schema.
	 * @param source
	 * @param schemaFilename
	 * @return
	 */
	public boolean isValid(String source, String schemaFilename);
	
}
