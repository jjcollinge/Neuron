package com.thing.rest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import com.thing.api.events.DataEvent;
import com.thing.api.events.DataEventListener;

public class SensorStreamResource implements DataEventListener {

	private static final Logger log = Logger
			.getLogger(SensorStreamResource.class.getName());

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String sensorId;
	String deviceId;

	private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
	private EventOutput eventOutput;

	public SensorStreamResource(UriInfo uriInfo, Request request,
			String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;

	}

	// GET: /devices/0/sensor/0/stream
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getConnection() {
		eventOutput = new EventOutput();

		initialiseSensorStreaming();
		BROADCASTER.add(eventOutput);
		return eventOutput;
	}
	
	public void finalize() {
		try {
			eventOutput.close();
		} catch (IOException e) {
			log.log(Level.INFO, "Couldn't close the SSE connection");
		}
	}
	
	public void initialiseSensorStreaming() {

		int id = Integer.valueOf(sensorId);
		DeviceController controller = new DeviceController(id);
		controller.addDataEventListener(this);
		controller.startSensorStreaming(id);

	}

	@Override
	public void onDataArrived(DataEvent dataEvent) {
		String data = dataEvent.getData();
		BROADCASTER.broadcast(new OutboundEvent.Builder().data(String.class, data).build());
	}
}
