package com.thing.rest;

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
import com.thing.api.model.AbstractDAOFactory;
import com.thing.api.model.Device;
import com.thing.api.model.DeviceDAO;
import com.thing.storage.DAOFactoryProducer;

/**
 * Root of RESTful services and is responsible for routing and sub resource
 * requests
 * 
 * @author jcollinge
 *
 */
@Path("devices")
@Consumes(MediaType.APPLICATION_JSON)
public class DevicesResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private static final Logger log = Logger
			.getLogger(DevicesResource.class.getName());

	private AbstractDAOFactory deviceDaoFactory;
	private DeviceDAO dao;
	private ResourceManager resources;

	public DevicesResource() {
		deviceDaoFactory = DAOFactoryProducer.getFactory("device");
		dao = deviceDaoFactory.getDeviceDAO("mongodb");
		resources = ResourceManager.getInstance();
	}

	/**
	 * GET: http://localhost:8080/thing-web/devices/hello
	 * 
	 * @return
	 */
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String greet() {
		return "Hello World";
	}

	/**
	 * GET: /devices
	 * 
	 * @return
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
	 * 
	 * @param id
	 * @return
	 */
	@Path("{device}")
	public DeviceResource getDevice(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request for resource: device " + id);
		return new DeviceResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/sensors
	 * 
	 * @param id
	 * @return
	 */
	@Path("{device}/sensors")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorsResource getDeviceSensors(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request for resource: devices/" + id + "/sensors");
		return new SensorsResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/sensors/0
	 * 
	 * @param id
	 * @param sid
	 * @return
	 */
	@Path("{device}/sensors/{sensorId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorResource getSensor(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		log.log(Level.INFO, "Get request for resource: devices/" + id + "/sensors" + sid);
		return new SensorResource(uriInfo, request, id, sid);
	}

	/**
	 * GET: /devices/0/sensors/0/stream Starts the sensor stream
	 * 
	 * @param id
	 * @param sid
	 * @return
	 */
	@Path("{device}/sensors/{sensorId}/stream")
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getSensorStream(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		SensorStreamResource SSR = new SensorStreamResource(uriInfo, request,
				id, sid);
		log.log(Level.INFO, "Get request for resource: devices/" + id + "/sensors/" + sid + "/stream");
		resources.addResource("devices/" + id + "/sensors/" + sid, SSR);
		return SSR.getConnection();
	}

	/**
	 * POST: /devices/0/sensors/0/stream Stops the sensor stream
	 * 
	 * @param id
	 * @param sid
	 * @return
	 */
	@Path("{device}/sensors/{sensorId}/stream")
	@POST
	public Response stopSensorStream(@PathParam("device") String id,
			@PathParam("sensorId") String sid) {
		log.log(Level.INFO, "Post request on resource: devices/" + id + "/sensors/" + sid + "/stream");
		Object res = resources.getResource("devices/" + id + "/sensors/" + sid);
		SensorStreamResource SSR = null;
		if(res != null) {
			SSR = (SensorStreamResource) res;
			if(SSR != null)
				SSR.stopSensorStreaming();
		}
		return null;
	}

	/**
	 * GET: /devices/0/actuators
	 * 
	 * @param id
	 * @return
	 */
	@Path("{device}/actuators")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ActuatorsResource getDeviceActuators(@PathParam("device") String id) {
		log.log(Level.INFO, "Get request on resource: devices/" + id + "/actuators");
		return new ActuatorsResource(uriInfo, request, id);
	}

	/**
	 * GET: /devices/0/actuators/0
	 * 
	 * @param id
	 * @param sid
	 * @return
	 */
	@Path("{device}/actuators/{actuatorId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ActuatorResource getActuator(@PathParam("device") String id,
			@PathParam("actuatorId") String sid) {
		log.log(Level.INFO, "Get request on resource: devices/" + id + "/actuators/" + sid);
		return new ActuatorResource(uriInfo, request, id, sid);
	}

	/**
	 * POST (option): /devices/0/actuators/0
	 * 
	 * @param id
	 * @param sid
	 * @param option
	 * @return
	 */
	@Path("{device}/actuators/{actuatorId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response operateActuator(@PathParam("device") String id,
			@PathParam("actuatorId") String sid, String optionJson) {
		log.log(Level.INFO, "Post request on resource: devices/" + id + "/actuators/" + sid);
		
		ActuatorResource actuator = new ActuatorResource(uriInfo, request, id,
				sid);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode option = null;
		try {
			option = mapper.readTree(optionJson);
			JsonNode node = option.get("data");
			if(node != null)
				actuator.invokeOperation(node.asText());
		} catch (Exception e) {
			log.log(Level.WARNING, "Dropping wrong format or corrupted POST to actuator");
		}
		return null;
	}
}
