package com.thing.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.thing.api.model.Actuator;
import com.thing.api.model.Device;
import com.thing.storage.MongoDBDeviceDAO;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActuatorResource {


	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String actuatorId;
	String deviceId;

	public ActuatorResource(UriInfo uriInfo, Request request, String deviceId, String actuatorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.actuatorId = actuatorId;

	}
	
	// GET: /devices/0/actuators/0
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Actuator getActuator() {
		
		System.out.println("Request for device");
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		Device device = dao.get(Integer.valueOf(deviceId));
		if(device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getActuator(Integer.valueOf(actuatorId));
		
	}
	
}
