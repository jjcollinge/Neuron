package com.neuron.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.components.DeviceProxy;
import com.neuron.api.components.DeviceProxyFactory;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.data.Device;
import com.neuron.api.data.Session;
import com.neuron.sessions.SessionController;

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
	private DeviceProxy proxy;
	
    public DeviceResource(UriInfo uriInfo, Request request, String id) {
    	
       this.uriInfo = uriInfo;
       this.request = request;
       this.id = id;
    }
	
	// POST: /devices/0/configure
	@POST
	public void configure(int refreshRate) {
		// Grab the devices session
		Session session = SessionController.getSingleton().getSession(Integer.valueOf(id));
		// Extract the sessions context
		com.neuron.api.data.Context context = session.getContext();
		proxy = new DeviceProxyFactory().getDeviceProxy(context);
		proxy.setup(Integer.valueOf(id).intValue());
		proxy.configureDevice(refreshRate);
	}
    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Device getDevice() {
		
		System.out.println("Request for device");
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
		Device device = dao.get(Integer.valueOf(id));
		return device;
	}

}
