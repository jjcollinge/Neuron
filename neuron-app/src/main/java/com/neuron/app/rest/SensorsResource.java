package com.neuron.app.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.data_access.DeviceDAOFactory;
import com.neuron.api.model.Device;
import com.neuron.api.model.Sensor;
import com.neuron.api.model.Session;
import com.neuron.api.proxy.DeviceProxy;
import com.neuron.api.proxy.DeviceProxyFactory;
import com.neuron.app.activities.sessionisation.SessionHandler;

/**
 * A representation of a list of sensors associated with a
 * particular device. Will handle calls to a particular
 * sensor and pass back the individual item.
 * @author JC
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	String deviceId;

	public SensorsResource(UriInfo uriInfo, Request request, String id) {
		
		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = id;
		
	}

	// GET: /devices/0/sensors
	@GET
	public ArrayList<Sensor> getSensors() {
		
		System.out.println("Request for device");
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
		Device device = dao.get(Integer.valueOf(deviceId));
		if(device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getSensors();
	}

	// GET: /devices/0/sensors/0
	@GET
	@Path("{sensorId}")
	public SensorResource getSensor(String deviceId,@PathParam("sensorId") String sensorId) {
		
		return new SensorResource(uriInfo, request, deviceId, sensorId);
	}
}
