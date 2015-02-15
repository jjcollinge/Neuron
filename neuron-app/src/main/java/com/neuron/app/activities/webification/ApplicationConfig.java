package com.neuron.app.activities.webification;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

	public ApplicationConfig() {

		super(SseFeature.class);
		packages("com.neuron.resources", "com.neuron.rest");
		property(ServerProperties.TRACING, "ALL");
	}

}
