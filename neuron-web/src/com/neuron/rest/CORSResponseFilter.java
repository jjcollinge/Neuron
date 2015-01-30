package com.neuron.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * Applies a filter to any outgoing response messages. Sets the headers
 * so that a client is aware of and can accept the data from the server.
 * This avoids issues with browser and languages which sometimes enforce
 * no CORS (Cross Origin Resource Sharing).
 * @author JC
 *
 */
@Provider
@PreMatching
public class CORSResponseFilter implements ContainerResponseFilter {

	private final static Logger log = Logger.getLogger( CORSResponseFilter.class.getName() );
	
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		
		log.log(Level.INFO, "Applying response filter");
		
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");	
		responseContext.getHeaders().add("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
		responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		responseContext.getHeaders().add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
		
	}

}
