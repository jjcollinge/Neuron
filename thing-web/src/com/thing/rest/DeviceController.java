package com.thing.rest;

import com.thing.api.events.DataEvent;
import com.thing.api.events.DataEventProducer;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.ParcelPacker;
import com.thing.api.model.Session;
import com.thing.connectors.BaseConnector;
import com.thing.connectors.ConnectorFactory;
import com.thing.connectors.impl.MqttConnector;
import com.thing.storage.MongoDBSessionDAO;

public class DeviceController extends DataEventProducer implements
		MessageEventListener {

	private Session session;
	private BaseConnector connector;

	public DeviceController(int sessionId) {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		session = dao.get(sessionId);
		ConnectorFactory factory = ConnectorFactory.getInstance();
		factory.registerConnectorType("MQTT", MqttConnector.class);
		connector = factory.getConnector(session);
		
		//SessionManager.getInstance().getMonitor().registerConnector(connector);
		connector.addMessageEventListener(this);
	}

	public void startSensorStreaming(int sensorId) {
		String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
		connector.subscribe(topic + "/stream/response", 2);
		connector.sendMessage(ParcelPacker.makeParcel("START_STREAM", "JSON",
				topic, "MQTT"));
	}

	public void stopSensorStreaming(int sensorId) {
		String topic = "devices/" + session.getId() + "/sensors/" + sensorId;
		connector.unsubscribe(topic + "/stream/response");
		connector.sendMessage(ParcelPacker.makeParcel("STOP_STREAM", "JSON",
				topic, "MQTT"));
	}
	
	public void invokeOperationOnActuator(int actuatorId, String option) {
		String topic = "devices/" + session.getId() + "/actuators/" + actuatorId;
//		connector.subscribe(topic + "/response", 2);
		connector.sendMessage(ParcelPacker.makeParcel(option, (String) session.getProperty("format"), topic, (String) session.getProperty("protocol")));
	}

	public void onMessageArrived(MessageEvent event) {
		String payload = event.getMessage().getPayload();
		DataEvent dataEvent = new DataEvent(this, payload);
		notifyListeners(dataEvent);
	}

}
