package com.thing.rest;

import java.io.IOException;
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

	private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
	private EventOutput eventOutput;
	private boolean streaming;

	public SensorStreamResource(UriInfo uriInfo, Request request,
			String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		
		streaming = false;

	}

	/**
	 * GET: /devices/0/sensor/0/stream/
	 * @return
	 */
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getConnection() {
		if(!streaming) {
			eventOutput = new EventOutput();
			startSensorStreaming();
			BROADCASTER.add(eventOutput);
			return eventOutput;
		}
		log.log(Level.INFO, "Already connected and streaming");
		return null;
	}
	
	/**
	 * Close down SSE connection
	 */
	public void finalize() {
		try {
			eventOutput.close();
		} catch (IOException e) {
			log.log(Level.INFO, "Couldn't close the SSE connection");
		}
	}
	
	/**
	 * start the sensor streaming data
	 */
	public void startSensorStreaming() {
		
		if(!streaming) {
			int id = Integer.valueOf(sensorId);
			DeviceProxy proxy = new DeviceProxyFactory().getDeviceProxy("mqtt");
			proxy.setup(Integer.valueOf(deviceId).intValue());
			proxy.addDataEventListener(this);
			proxy.startSensorStreaming(id);
			streaming = true;
		} else {
			log.log(Level.INFO, "Sensor is already streaming");
		}
	}
	
	/**
	 * Stop the sensor streaming data
	 */
	public void stopSensorStreaming() {
		
		if(streaming) {
			int id = Integer.valueOf(sensorId);
			DeviceProxy proxy = new DeviceProxyFactory().getDeviceProxy("mqtt");
			proxy.setup(Integer.valueOf(deviceId).intValue());
			proxy.addDataEventListener(this);
			proxy.stopSensorStreaming(id);
			streaming = false;
			log.log(Level.INFO, "Stopped sensor streaming");
		}
	}

	/**
	 * Called when a new data event is received
	 */
	@Override
	public void onDataArrived(DataEvent dataEvent) {
		String data = dataEvent.getData();
		BROADCASTER.broadcast(new OutboundEvent.Builder().data(String.class, data).build());
		log.log(Level.INFO, "Sending new SSE data event");
	}
}
