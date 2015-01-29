package com.thing.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CORSRequestFilter implements ContainerRequestFilter {

	private final static Logger log = Logger.getLogger( CORSRequestFilter.class.getName() );
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		
		log.log(Level.INFO, "Applying request filter");
		
		if( requestContext.getRequest().getMethod().equals( "OPTIONS" )) {
			
			log.log(Level.INFO, "Browser pre-flight HTTP Method OPTIONS detected!");
			
			requestContext.abortWith( Response.status( Response.Status.OK ).build() );
		} 
		
		//log.log(Level.INFO, "Method detected: " + requestContext.getRequest().getMethod());
		//requestContext.abortWith( Response.status( Response.Status.OK ).build() );
	}

}
