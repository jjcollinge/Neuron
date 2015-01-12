package com.thing.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

public class SensorStreamResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String sensorId;
	String deviceId;
	
	public SensorStreamResource(UriInfo uriInfo, Request request, String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;

	}
	
	// GET: /devices/0/sensor/0/stream
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getMessages() {

		final EventOutput eventOutput = new EventOutput();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 10; i++) {
						// ... code that waits 1 second
						final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
						eventBuilder.name("message-to-client");
						eventBuilder.data(String.class, "Hello world " + i
								+ "!");
						final OutboundEvent event = eventBuilder.build();
						eventOutput.write(event);
					}
				} catch (IOException e) {
					throw new RuntimeException("Error when writing the event.",
							e);
				} finally {
					try {
						eventOutput.close();
					} catch (IOException ioClose) {
						throw new RuntimeException(
								"Error when closing the event output.", ioClose);
					}
				}
			}
		}).start();
		return eventOutput;
	}

}
