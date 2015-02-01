package com.neuron.resources;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuron.api.components.dal.AbstractDAOFactory;
import com.neuron.api.components.dal.DAOFactoryProducer;
import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.data.Device;
import com.neuron.rest.ResourceManager;

/**
 * Device resources root. Any resource associated with a device
 * will be accessible as a derived uri of this entry point. A
 * GET request to this uri will return a list of all device
 * representations currently stored within the system. 
 * @author JC
 *
 */
@Path("devices")
@Consumes(MediaType.APPLICATION_JSON)
public class DevicesResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private static final Logger log = Logger.getLogger(DevicesResource.class
			.getName());

	private AbstractDAOFactory deviceDaoFactory;
	private DeviceDAO dao;
	private ResourceManager resources;

	/**
	 * A new Resource will be created per request. Any persistence in the
	 * web app needs to be independent of this class and initialised from
	 * here.
	 */
	public DevicesResource() {
		deviceDaoFactory = DAOFactoryProducer.getFactory("device");
		dao = deviceDaoFactory.getDeviceDAO();
		resources = ResourceManager.getInstance();
	}
	
	/**
	 * GET: /devices/hello
	 * @return String Hello World string
	 */
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String greet() {
		return "Hello World";
	}

	/**
	 * GET: /devices
	 * @return A representation of all the devices current stored in the
	 * system
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Device> getDevices() {
		log.log(Level.INFO, "Get request for resource: devices");
		ArrayList<Device> devices = (ArrayList<Device>) dao.getAll();
		if (devices == null) {
			throw new RuntimeException("Devices not found");
		}
		return devices;
	}

	/**
	 * GET: /devices/0
	 * @param id The id of the particular device to retrieve
	 * @return a single device representation
	 */
	@Path("{device}")
	public DeviceResource getDevice(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request for resource: device " + id);
		return new DeviceResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/sensors
	 * @param id The id of a particular device to retrieve
	 * @return a representation of all the sensors available in a single device representation
	 */
	@Path("{device}/sensors")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorsResource getDeviceSensors(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request for resource: devices/" + id
				+ "/sensors");
		return new SensorsResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/sensors/0
	 * @param id The id of a particular device to retrieve 
	 * @param sid The id of a particular sensor to retrieve
	 * @return a representation of a single sensor from a single device representation
	 */
	@Path("{device}/sensors/{sensorId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorResource getSensor(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		log.log(Level.INFO, "Get request for resource: devices/" + id
				+ "/sensors" + sid);
		return new SensorResource(uriInfo, request, id, sid);
	}

	/**
	 * GET: /devices/0/sensors/0/stream Starts the sensor stream
	 * @param id The id of a particular device to retrieve
	 * @param sid The id of a particular sensor to retrieve
	 * @return opens a connection with the client and will either periodically or on event
	 * send data down the connection to the client. This will remain open until both ends
	 * close and cleanup the connection.
	 */
	@Path("{device}/sensors/{sensorId}/stream")
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getSensorStream(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		String resourceURI = "devices/"+id+"/sensors/"+sid;
		SensorStreamResource SSR = (SensorStreamResource) resources.getResource(resourceURI);
		if(SSR != null) {
			log.log(Level.INFO, "Resource already exists");
		} else {
			log.log(Level.INFO, "No existing resource, creating a new one");
			SSR = new SensorStreamResource(uriInfo, request, id, sid);
			resources.addResource(resourceURI, SSR);
		}
		return SSR.getConnection();
	}

	/**
	 * POST: /devices/0/sensors/0/stream Stops the sensor stream
	 * @param id The id of a particular device to retrieve
	 * @param sid  The id of a particular sensor to retrieve
	 * @return OK response, this will signify the connection has been terminated succesfully
	 */
	@Path("{device}/sensors/{sensorId}/stream")
	@POST
	public Response stopSensorStream(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		String resourceURI = "devices/"+id+"/sensors/"+sid;
		SensorStreamResource SSR = (SensorStreamResource) resources.getResource(resourceURI);
		if(SSR != null) {
			SSR.stopSensorStreaming();
		} else {
			log.log(Level.INFO, "Failed to stop sensor as resource doesn't exist");
		}
		return Response.ok().build();
	}

	/**
	 * GET: /devices/0/actuators
	 * @param id The id of a particular device to retrieve
	 * @return a representation of all the actuators available in a single device representation
	 */
	@Path("{device}/actuators")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ActuatorsResource getDeviceActuators(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request on resource: devices/" + id
				+ "/actuators");
		return new ActuatorsResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/actuators/0
	 * @param id The id of a particular device to retrieve
	 * @param sid The id of a particular actuator to retrieve
	 * @return a representation of a single actuators from a single device representation
	 */
	@Path("{device}/actuators/{actuatorId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ActuatorResource getActuator(@PathParam("device") String id,
			@PathParam("actuatorId") String sid) {
		log.log(Level.INFO, "Get request on resource: devices/" + id
				+ "/actuators/" + sid);
		return new ActuatorResource(uriInfo, request, id, sid);
	}

	/**
	 * POST (option): /devices/0/actuators/0
	 * @param id The id of a particular device to retrieve
	 * @param sid The id of a particular actuator to operate
	 * @param optionJson A predefined option (command) to send to the device
	 * @return Response OK will signify the command has been successfully sent
	 */
	@Path("{device}/actuators/{actuatorId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response operateActuator(@PathParam("device") String id,
			@PathParam("actuatorId") String sid, String optionJson) {
		log.log(Level.INFO, "Post request on resource: devices/" + id
				+ "/actuators/" + sid);

		ActuatorResource actuator = new ActuatorResource(uriInfo, request, id,
				sid);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode option = null;
		try {
			option = mapper.readTree(optionJson);
			JsonNode node = option.get("data");
			if (node != null)
				actuator.invokeOperation(node.asText());
		} catch (Exception e) {
			log.log(Level.WARNING,
					"Dropping wrong format or corrupted POST to actuator");
		}
		return Response.ok().build();
	}
}
