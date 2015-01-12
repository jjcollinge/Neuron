package com.thing.sessions;

import com.thing.api.events.DataEvent;
import com.thing.api.events.DataEventProducer;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.impl.MqttConnector;

public class DeviceController extends DataEventProducer implements MessageEventListener {

	private SessionManager sessionManager;
	private Session session;
	private BaseConnector connector;
	
	public DeviceController(int deviceId) {
		sessionManager = SessionManager.getInstance();
		this.session = sessionManager.getSession(deviceId);
		if(session.getProtocol().equals("MQTT")) {
			connector = new MqttConnector();
			connector.connect("localhost", "1883");
			connector.addMessageEventListener(this);
		}
	}
	
	public void startSensorStreaming(int sensorId) {
		String topic = "devices/"+session.getDeviceId()+"/sensors/"+sensorId;
		connector.subscribe(topic + "/stream/response", 2);
		connector.sendMessage(ParcelPacker.makeParcel("START_STREAM",  "JSON", topic, "MQTT"));
	}
	
	public void stopSensorStreaming(int sensorId) {
		String topic = "devices/"+session.getDeviceId()+"/sensors/"+sensorId;
		connector.unsubscribe(topic + "/stream/response");
		connector.sendMessage(ParcelPacker.makeParcel("STOP_STREAM",  "JSON", topic, "MQTT"));
	}

	public void onMessageArrived(MessageEvent event) {
		String payload = event.getMessage().getPayload();
		DataEvent dataEvent = new DataEvent(this, payload);
		notifyListeners(dataEvent);
	}
	
	
	
}
