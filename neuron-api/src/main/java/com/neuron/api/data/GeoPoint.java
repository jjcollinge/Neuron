package com.neuron.api.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Provides a POJO representation of a geo location.
 * Will be constructed by deserialization.
 * NOTE: Currently only supports JSON deserialization.
 * @author JC
 * 
 */
public class GeoPoint {
	
	@JsonProperty("longitude")
	private double longitude;
	@JsonProperty("latitude")
	private double latitude;
	
	public GeoPoint() {
		
	}
	
	public GeoPoint(double lon, double lat) {
		this.longitude = lon;
		this.latitude = lat;
	}
	
	// Setters
	public void setLongitude(double lon) {
		this.longitude = lon;
	}
	
	public void setLatitude(double lat) {
		this.latitude = lat;
	}

	// Getters
	public double getLongitude() {
		return this.longitude;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
}
