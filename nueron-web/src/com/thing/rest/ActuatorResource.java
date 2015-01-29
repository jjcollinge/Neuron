package com.thing.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.data.Actuator;
import com.neuron.api.data.Device;

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
	
	/**
	 * GET: /devices/0/actuators/0
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Actuator getActuator() {
		
		System.out.println("Request for device");
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO("mongodb");
		Device device = dao.get(Integer.valueOf(deviceId));
		if(device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getActuator(Integer.valueOf(actuatorId));
		
	}
	
	/**
	 * Sends a one way message to the device
	 * @param option
	 * @return
	 */
	public Response invokeOperation(String option) {
		
		DeviceProxy proxy = new DeviceProxyFactory().getDeviceProxy("mqtt");
		proxy.setup(Integer.valueOf(deviceId));
		proxy.operateActuator(Integer.valueOf(actuatorId), option);
		
		// get POST data and call invoke on deviceController
		return null;
	}
	
}
