package com.thing.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class GeoPoint {
	
	@JsonProperty("longitude")
	private double longitude;
	@JsonProperty("latitude")
	private double latitude;
	
	public GeoPoint(double lon, double lat) {
		this.longitude = lon;
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
