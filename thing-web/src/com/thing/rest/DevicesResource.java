package com.thing.rest;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import com.thing.api.model.Device;
import com.thing.storage.DataHandler;

// URL: http://localhost:8080/thing-web/devices/hello

@Path("devices")
@Consumes(MediaType.APPLICATION_JSON)
public class DevicesResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String greet() {
		return "Hello World";
	}

	// GET: /devices
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Device> getDevices() {
		System.out.println("Request for devices");
		ArrayList<Device> devices = DataHandler.getInstance().getDevices();
		if (devices == null) {
			throw new RuntimeException("Devices not found");
		}
		return devices;
	}

	// GET: /devices/0
	@Path("{device}")
	public DeviceResource getDevice(@PathParam("device") String id) {
		return new DeviceResource(uriInfo, request, id);
	}
	
	// GET: /devices/0/sensors
	@Path("{device}/sensors")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorsResource getDeviceSensors(@PathParam("device") String id) {
		return new SensorsResource(uriInfo, request, id);
	}
	
	// GET: /devices/0/sensors/0
	@Path("{device}/sensors/{sensorId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SensorResource getSensor(@PathParam("device") String id, @PathParam("sensorId") String sid) {
		return new SensorResource(uriInfo, request, id, sid);
	}
	
	// GET: /devices/0/sensors/0/stream
	@Path("{device}/sensors/{sensorId}/stream")
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getSensorStream(@PathParam("device") String id, @PathParam("sensorId") String sid) {
		SensorStreamResource SSR = new SensorStreamResource(uriInfo, request, id, sid);
		return SSR.getValue();
	}

}
