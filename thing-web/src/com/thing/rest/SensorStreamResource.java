package com.thing.rest;

import java.io.IOException;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import com.thing.api.events.DataEvent;
import com.thing.api.events.DataEventListener;

public class SensorStreamResource implements DataEventListener {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String sensorId;
	String deviceId;
	
	LinkedList<String> valueStream;

	public SensorStreamResource(UriInfo uriInfo, Request request,
			String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		
		valueStream = new LinkedList<String>();

	}

	// GET: /devices/0/sensor/0/stream
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getValue() {

		final EventOutput eventOutput = new EventOutput();
		final DataEventListener _this = this;
		
		DeviceController controller = new DeviceController(Integer.valueOf(deviceId));
		controller.addDataEventListener(_this);
		controller.startSensorStreaming(Integer.valueOf(sensorId));
				
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

		return eventOutput;
	}

	@Override
	public void onDataArrived(DataEvent dataEvent) {
		valueStream.addLast(dataEvent.getData());
		getValue();
	}

}
