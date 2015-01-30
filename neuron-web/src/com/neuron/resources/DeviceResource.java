package com.neuron.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.data.Device;

/**
 * A representation of an in system device. Will only return
 * a data represenation of itself including any
 * sensors or actuators associated with it.
 * @author JC
 *
 */
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
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
		Device device = dao.get(Integer.valueOf(id));
		if(device == null) {
			throw new RuntimeException("Device " + id + " not found");
		}
		return device;
	}

}
