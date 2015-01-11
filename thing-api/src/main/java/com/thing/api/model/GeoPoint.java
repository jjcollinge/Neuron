package com.thing.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class GeoPoint {
	
	@JsonProperty("longitude")
	private double longitude;
	@JsonProperty("latitude")
	private double latitude;
	
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
