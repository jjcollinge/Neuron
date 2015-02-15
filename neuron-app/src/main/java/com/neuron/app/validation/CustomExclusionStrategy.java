package com.neuron.app.validation;

import java.util.ArrayList;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Name: CustomExclusionStrategy
 * ---------------------------------------------------------------
 * Desc: Restricts the classes Gson attempts to deserialize when
 * 		 using fromJson
 * 
 * @author jcollinge
 *
 */
public class CustomExclusionStrategy implements ExclusionStrategy {

	private ArrayList<Class<?>> excludedClasses;

   public CustomExclusionStrategy(ArrayList<Class<?>>  excludedClasses) {
     this.excludedClasses = excludedClasses;
   }

   public boolean shouldSkipClass(Class<?> clazz) {
     if(excludedClasses.contains(clazz))
    	 return true;
     else
    	 return false;
   }

   public boolean shouldSkipField(FieldAttributes f) {
     if(excludedClasses.contains(f.getDeclaredClass()))
    	return true;
     else
    	 return false;
   }

}
