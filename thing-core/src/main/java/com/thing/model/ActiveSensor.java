package com.thing.model;

import java.util.ArrayList;
import java.util.LinkedList;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.messaging.Connector;
import com.thing.sessions.Session;

public class ActiveSensor implements MessageEventListener {

	private int id = 0;
	private LinkedList<String> values;
	private String previousValue;
	private ArrayList<MessageEventListener> listeners = null;
	private String sense;
	private String unit;
	private String type;
	private Connector connector = null;
	private Session session = null;
	
	public ActiveSensor(String sense, String unit, String type) {
		this.sense = sense;
		this.unit = unit;
		this.type = type;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void initialise(Connector connector, Session session) {
		this.values = new LinkedList<String>();
		this.listeners = new ArrayList<MessageEventListener>();
		this.previousValue = "Preparing...";
		setSession(session);
		setConnector(connector);
		update();
	}
	private void setConnector(Connector connector) {
		this.connector = connector;
		connector.connect("localhost", "1883");
		connector.subscribe("devices/"+session.getDeviceId()+"/sensors/"+id+"/response", 2);
		connector.addMessageEventListener(this);
	}
	private void setSession(Session session) {
		this.session = session;
	}
	public String getSense() {
		return this.sense;
	}
	public String getUnit() {
		return this.unit;
	}
	public String getType() {
		return this.type;
	}
	public String get() {
		update();
		if(!values.isEmpty()) {
			previousValue = values.pollFirst(); 
		}
		return previousValue;
	}
	public void addSensorEventListener(MessageEventListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	public void removeSensorEventListener(MessageEventListener listener) {
		listeners.remove(listener);
	}
	public void onMessageArrived(MessageEvent event) {
		session.unlock();
		String valueString = event.getMessage().getPayload();
		System.out.println("Sensor updated, unlocked session: " + valueString);
		this.values.addLast(valueString);
		for(MessageEventListener listener : listeners){
			listener.onMessageArrived(event);
		}
	}
	private void update() {
		String topic =  "devices/"+session.getDeviceId()+"/sensors/"+id;
		String request = "GET";
		Parcel parcel = ParcelPacker.makeParcel(request, session.getFormat(), topic, session.getProtocol());
		session.lockOnSend();
		connector.sendMessage(parcel, session);
	}
}
