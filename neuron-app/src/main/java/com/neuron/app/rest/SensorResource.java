package com.neuron.app.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.data_access.DeviceDAOFactory;
import com.neuron.api.model.Device;
import com.neuron.api.model.Sensor;

/**
 * A representation of an in system sensor. This is only the data
 * representation and all interaction has been moved out into a 
 * separate streaming resource. @see com.neuron.resources.SensorStreamResource
 * @author JC
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String sensorId;
	String deviceId;

	public SensorResource(UriInfo uriInfo, Request request, String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;

	}
	
	// GET: /devices/0/sensors/0
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Sensor getSensor() {
		
		System.out.println("Request for device");
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
		Device device = dao.get(Integer.valueOf(deviceId));
		if(device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getSensor(Integer.valueOf(sensorId));
		
	}

}
