package com.thing.rest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
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
	
	private LinkedList<String> valueStream;
	private DeviceController controller;
	private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
	private boolean streaming;
	
	public SensorStreamResource(UriInfo uriInfo, Request request,
			String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		
		valueStream = new LinkedList<String>();

		final DataEventListener _this = this;
		
		controller = new DeviceController(Integer.valueOf(deviceId));
		controller.addDataEventListener(_this);
		
		streaming = false;
	}

	// GET: /devices/0/sensor/0/stream
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getValue() {

		final EventOutput eventOutput = new EventOutput();
		
		if(!streaming) {
			log.log(Level.INFO, "Requesting for stream to start");
			// Start streaming
			int id = Integer.valueOf(sensorId);
			controller.startSensorStreaming(id);
			streaming = true;
		}
				
		if(!valueStream.isEmpty()) {
			final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
			eventBuilder.name("sensor-data");
			eventBuilder.data(String.class, valueStream.pop());
			final OutboundEvent event = eventBuilder.build();
			try {
				eventOutput.write(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.log(Level.INFO, "Returning eventOutput " + eventOutput.toString());
		return eventOutput;
	}

	@Override
	public void onDataArrived(DataEvent dataEvent) {
		log.log(Level.INFO, "New value arrived, adding to queue");
		valueStream.addLast(dataEvent.getData());
		getValue();
	}

}
