package com.thing.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

	public ApplicationConfig() {
		
		super(SseFeature.class);
		
		packages("com.thing.rest");

		property(ServerProperties.TRACING, "ALL");
		
	}
	
}
