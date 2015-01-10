package com.thing.sessions.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Name: Ping
 * ---------------------------------------------------------------
 * Desc: A model for a Ping message (Used to cast into a POJO from JSON)
 * 
 * @author jcollinge
 *
 */
public class Ping {

	@JsonProperty("id")
	int id;
	
	//Setters
	public void setId(int id) {
		this.id = id;
	}
	//Getters
	public int getId(){
		return this.id;
	}
}
