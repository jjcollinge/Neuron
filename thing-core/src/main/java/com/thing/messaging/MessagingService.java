package com.thing.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Service;
import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;
import com.thing.api.messaging.Message;
import com.thing.api.messaging.Messenger;
import com.thing.api.messaging.Parcel;
import com.thing.api.messaging.ParcelPacker;
import com.thing.sessions.SessionManager;

public class MessagingService implements MessageEventListener, Service {

	private static final Logger log = Logger.getLogger( MessagingService.class.getName() );
	
	private HashMap<Integer, Messenger> messangers;
	private HashMap<String, ArrayList<MessageEventListener>> subscriptions;
	private static MessagingService instance;
	private SessionManager sessionManager;
	
	private MessagingService() {
		log.log(Level.INFO, "Creating MessagingService");
		messangers = new HashMap<Integer, Messenger>();
		subscriptions = new HashMap<String, ArrayList<MessageEventListener>>();
	}
	
	public static MessagingService getInstance() {
		if(instance == null) {
			instance = new MessagingService();
		}
		return instance;
	}
	
	public void start() {

		log.log(Level.INFO, "Starting service...");
		Messenger serial = new SerialMessanger(SessionManager.generateId());
		serial.connect("", "/dev/ttyACM0");
		Messenger mqtt = new MqttMessanger(SessionManager.generateId());
		mqtt.connect("localhost", "1883");
		messangers.put(serial.getId(), serial);
		messangers.put(mqtt.getId(), mqtt);
		
		for(Integer id : messangers.keySet()) {
			messangers.get(id).addMessageEventListener(this);
		}
	}
	
	public void stop() {
		
		log.log(Level.INFO, "Stopping service...");
		for(Integer id : messangers.keySet()) {
			messangers.get(id).removeMessageEventListener(this);
		}
		
	}

	public void sendMessage(Parcel message) {
		int id = message.getMessage().getId();
		messangers.get(id).send(message);
	}
	
	public void sendMessage(Message message, String topic) {
		Parcel parcel = ParcelPacker.makeParcel(message, topic);
		sendMessage(parcel);
	}
	
	public void subscribe(String topic, int qos, MessageEventListener subscriber) {
		ArrayList<MessageEventListener> topicSubscribers = subscriptions.get(topic);
		if(topicSubscribers == null) {
			// No subscribers for the topic yet
			topicSubscribers = new ArrayList<MessageEventListener>();
		}
		topicSubscribers.add(subscriber);
		subscriptions.put(topic, topicSubscribers);

		// tell messangers to start collecting on the topic
		for(Integer id : messangers.keySet()) {
			messangers.get(id).subscribe(topic, qos);
		}
		log.log(Level.INFO, "Successfully subscribed " +  subscriber.getClass().getSimpleName() + " to topic " + topic);
	}
	
	public void unsubscribe(String topic, MessageEventListener subscriber) {
		ArrayList<MessageEventListener> subscribers = subscriptions.get(topic);
		if(subscribers == null) {
			// No subscribers for the topic yet
			return;
		}
		subscribers.remove(subscriber);
		if(subscribers.isEmpty()) {
			// if last subscriber removed
			subscriptions.remove(topic);
		} else {
			subscriptions.put(topic, subscribers);
		}
		
		// stop the messangers collecting on that topic
		for(Integer id : messangers.keySet()) {
			messangers.get(id).unsubscribe(topic);
		}
		log.log(Level.INFO, "Successfully unsubscribed " +  subscriber.getClass().getSimpleName() + " from topic " + topic);
	}

	public void onMessageArrived(MessageEvent event) {
		
		RoutedMessageEvent e = (RoutedMessageEvent) event;
		ArrayList<MessageEventListener> subscribers = subscriptions.get(e.getTopic());
		if(subscribers != null) {
			for(MessageEventListener subscriber : subscribers) {
				subscriber.onMessageArrived(event);
			}
		}
	}

}
