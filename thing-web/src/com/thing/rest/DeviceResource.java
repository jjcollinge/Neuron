package com.thing.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.thing.api.model.Device;
import com.thing.storage.DataHandler;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	
    public DeviceResource(UriInfo uriInfo, Request request, String id) {
    	
       this.uriInfo = uriInfo;
       this.request = request;
       this.id = id;
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDevice() {
		
		System.out.println("Request for device");
		Device device = DataHandler.getInstance().getDevice(Integer.valueOf(id));
		if(device == null) {
			throw new RuntimeException("Device " + id + " not found");
		}
		return device;
	}

}
