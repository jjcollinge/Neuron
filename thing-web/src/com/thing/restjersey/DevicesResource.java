package com.thing.restjersey;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.thing.api.model.Device;
import com.thing.storage.DataHandler;

@Path("/devices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class DevicesResource {

	// Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Device> getDevices() {
		System.out.println("Request for devices");
		ArrayList<Device> devices = DataHandler.getInstance().getDevices();
		if(devices == null) {
			throw new RuntimeException("Devices not found");
		}
		return devices;
	}
	
	//Defines that the next path parameter after todos is
	// treated as a parameter and passed to the TodoResources
	// Allows to type http://localhost:8080/de.vogella.jersey.todo/rest/todos/1
	// 1 will be treaded as parameter todo and passed to TodoResource
	@Path("{device}")
	public DeviceResource getDevice(@PathParam("device") String id) {
		return new DeviceResource(uriInfo, request, id);
	}

}
