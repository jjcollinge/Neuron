package com.neuron.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Applies a filter to any incoming requests. The filter will
 * accept any preflight HTTP messages sent from the browser
 * to authenticate CORS(Cross Origin Resource Sharing) which
 * can sometime be blocked in languages such as javascript.
 * @author JC
 *
 */
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
