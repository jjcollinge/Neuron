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

import com.neuron.api.core.Controller;
import com.neuron.api.events.DataEvent;
import com.neuron.api.events.DataEventListener;
import com.neuron.api.model.Session;
import com.neuron.api.proxy.DeviceProxy;
import com.neuron.api.proxy.DeviceProxyFactory;
import com.neuron.app.activities.sessionisation.SessionHandler;

/**
 * Handles dispatching start and stop keywords to a device
 * to interact with a specific sensor. Listens for any
 * responding values from the sensor and adds them to the
 * SSE broadcast which will then push them to the client.
 * @author JC
 *
 */
public class SensorStreamResource implements DataEventListener {

	private static final Logger log = Logger
			.getLogger(SensorStreamResource.class.getName());

	/**
	 * HTTP context information about the request
	 */
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String sensorId;
	String deviceId;

	private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
	private EventOutput eventOutput;
	private volatile boolean streaming;
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
	 * @return SSE connection, this will stay open until terminated by the server or
	 * manually destroyed by the client
	 */
	@GET
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput getConnection() {

		eventOutput = new EventOutput();
		BROADCASTER.add(eventOutput);
		startSensorStreaming();
			
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
			Controller controller = Controller.getApplication();
			SessionHandler sessionHandler = (SessionHandler) controller.getActivity("Session").getService("SessionHandler");
			Session session = sessionHandler.getSession(Integer.valueOf(deviceId));
			// Extract the sessions context
			com.neuron.api.model.Context context = session.getContext();
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
			DataEvent event = new DataEvent(this, "{ \"sessionId\": \"" + deviceId + "\", \"data\": \"CLOSE\" }");
			onDataArrived(event);
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
		} else {
			log.log(Level.INFO, "Sensor isn't streaming");
		}
	}

	/**
	 * Called when a new data event is received from a device
	 */
	@Override
	public void onDataArrived(DataEvent dataEvent) {
		String data = (String) dataEvent.getData();
		if(streaming) {
			BROADCASTER.broadcast(new OutboundEvent.Builder().data(String.class, data).build());
			log.log(Level.INFO, "Sending new SSE data event");
		}
	}
}
