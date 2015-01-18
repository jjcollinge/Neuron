package com.thing.rest;

import com.thing.api.components.DeviceProxy;
import com.thing.api.events.DataEvent;
import com.thing.api.events.DataEventProducer;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;
import com.thing.storage.MongoDBSessionDAO;

public class MqttDeviceProxy extends DataEventProducer implements
		MessageEventListener, DeviceProxy {

	private Session session;
	private BaseConnector connector;

	public MqttDeviceProxy(int sessionId) {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		session = dao.get(sessionId);
		ConnectorFactory factory =  new ConnectorFactory();
		connector = factory.getConnector(session);
		connector.addMessageEventListener(this);
	}
	@Override
	public void startSensorStreaming(int sensorId) {
		String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
		connector.subscribe(topic + "/stream/response", 2);
		connector.sendMessage(ParcelPacker.makeParcel("START_STREAM", "JSON",
				topic, "MQTT"));
	}
	@Override
	public void stopSensorStreaming(int sensorId) {
		String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
		connector.unsubscribe(topic + "/stream/response");
		connector.sendMessage(ParcelPacker.makeParcel("STOP_STREAM", "JSON",
				topic, "MQTT"));
	}
	@Override
	public void operateActuator(int actuatorId, String option) {
		String topic = "devices/" + session.getId() + "/actuators/" + actuatorId;
		connector.sendMessage(ParcelPacker.makeParcel(option, (String) session.getProperty("format"), topic, (String) session.getProperty("protocol")));
	}
	@Override
	public void onMessageArrived(MessageEvent event) {
		String payload = event.getMessage().getPayload();
		DataEvent dataEvent = new DataEvent(this, payload);
		notifyListeners(dataEvent);
	}

}
