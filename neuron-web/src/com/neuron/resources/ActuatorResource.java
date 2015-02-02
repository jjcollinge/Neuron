package com.neuron.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.neuron.api.components.DeviceProxy;
import com.neuron.api.components.DeviceProxyFactory;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.data.Actuator;
import com.neuron.api.data.Device;
import com.neuron.api.data.Session;
import com.neuron.sessions.SessionController;

/**
 * A representation of an actuator in the system. Can either
 * return a data representation of itself or invoke operations
 * on the actual actuator.
 * @author JC
 *
 */
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
	 * @return Actuator The desired actuator
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Actuator getActuator() {
		
		System.out.println("Request for device");
		DeviceDAO dao = new DeviceDAOFactory().getDeviceDAO();
		Device device = dao.get(Integer.valueOf(deviceId));
		if(device == null) {
			throw new RuntimeException("Device " + deviceId + " not found");
		}
		return device.getActuator(Integer.valueOf(actuatorId));
		
	}
	
	/**
	 * Sends a one way message to the device
	 * @param option The option to invoke on the device
	 * @return Response The success of the operation
	 */
	public Response invokeOperation(String option) {
		
		// Not streaming so tell device to start publishing
		int id = Integer.valueOf(actuatorId);
		// Grab the devices session
		Session session = SessionController.getInstance().getSession(Integer.valueOf(deviceId));
		// Extract the sessions context
		com.neuron.api.data.Context context = session.getContext();
		DeviceProxy proxy = new DeviceProxyFactory().getDeviceProxy(context);
		proxy.setup(Integer.valueOf(deviceId).intValue());
		proxy.operateActuator(id, option);
		// get POST data and call invoke on deviceController
		return Response.ok().build();
	}
	
}
