package com.neuron.app.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.data_access.AbstractDAOFactory;
import com.neuron.api.data_access.DAOFactoryProducer;
import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.model.Actuator;
import com.neuron.api.model.Device;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActuatorsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	String deviceId;

	public ActuatorsResource(UriInfo uriInfo, Request request, String id) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = id;

	}

	// GET: /devices/0/actuators
	@GET
	public ArrayList<Actuator> getActuator() {

		System.out.println("Request for device");
		AbstractDAOFactory daoFactory = DAOFactoryProducer.getFactory("device");
		DeviceDAO dao = daoFactory.getDeviceDAO();
		Device device = dao.get(Integer.valueOf(deviceId));
		if (device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getActuators();

	}

}
