package com.neuron.resources;

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


import com.neuron.api.data.Session;
import com.neuron.api.events.DataEvent;
import com.neuron.api.events.DataEventListener;
import com.neuron.rest.DeviceProxy;
import com.neuron.rest.DeviceProxyFactory;
import com.neuron.sessions.SessionController;

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
	private DeviceProxy proxy;

	public SensorStreamResource(UriInfo uriInfo, Request request,
			String deviceId, String sensorId) {

		this.uriInfo = uriInfo;
		this.request = request;
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		this.streaming = false;
	}

	/**
	 * GET: /devices/0/sensor/0/stream/
	 * @return
	 */
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getConnection() {

		eventOutput = new EventOutput();
		startSensorStreaming();
		BROADCASTER.add(eventOutput);
		log.log(Level.INFO, "Added eventOutput to broadcaster");
		return eventOutput;
	}
	
	/**
	 * Close down SSE connection
	 */
	public void finalize() {
		try {
			BROADCASTER.remove(eventOutput);
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
			// Not streaming so tell device to start publishing
			int id = Integer.valueOf(sensorId);
			// Grab the devices session
			Session session = SessionController.getInstance().getSession(Integer.valueOf(deviceId));
			// Extract the sessions context
			com.neuron.api.data.Context context = session.getContext();
			proxy = new DeviceProxyFactory().getDeviceProxy(context);
			proxy.setup(Integer.valueOf(deviceId).intValue());
			proxy.addDataEventListener(this);
			proxy.startSensorStreaming(id);
			streaming = true;
			log.log(Level.INFO, "Sensor is now streaming");
		} else {
			log.log(Level.INFO, "Sensor is already streaming");
		}
	}
	
	/**
	 * Stop the sensor streaming data
	 */
	public void stopSensorStreaming() {
		
		if(streaming) {
			// Sensor is publishing so tell it to stop
			int id = Integer.valueOf(sensorId);
			proxy.stopSensorStreaming(id);
			proxy.removeDataEventListener(this);
			streaming = false;
			try {
				if(eventOutput != null) {
					BROADCASTER.remove(eventOutput);
					eventOutput.close();
					log.log(Level.INFO, "Removed eventOuput from broadcaster and closed stream");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.log(Level.INFO, "Stopped sensor streaming");
		}
	}

	/**
	 * Called when a new data event is received
	 */
	@Override
	public void onDataArrived(DataEvent dataEvent) {
		String data = (String) dataEvent.getData();
		BROADCASTER.broadcast(new OutboundEvent.Builder().data(String.class, data).build());
		log.log(Level.INFO, "Sending new SSE data event");
	}
}
