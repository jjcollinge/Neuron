package com.thing.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import sessions.SessionManager;

import com.google.gson.Gson;

public class MessagingService implements MessageEventListener {

	private static final Logger log = Logger.getLogger( MessagingService.class.getName() );
	
	private HashMap<Integer, Messanger> messangers;
	private HashMap<String, ArrayList<MessageListener>> subscriptions;
	private static MessagingService instance;
	
	private MessagingService() {
		log.log(Level.INFO, "Creating MessagingService");
		messangers = new HashMap<Integer, Messanger>();
		subscriptions = new HashMap<String, ArrayList<MessageListener>>();
		instance = new MessagingService();
	}
	
	public static MessagingService getService() {
		if(instance == null) {
			instance = new MessagingService();
		}
		return instance;
	}
	
	public void start() {
		
		Messanger serial = new SerialMessanger(SessionManager.generateId());
		serial.connect("", "/dev/ttyACM0");
		Messanger mqtt = new MqttMessanger(SessionManager.generateId());
		mqtt.connect("localhost", "1883");
		messangers.put(serial.getId(), serial);
		messangers.put(mqtt.getId(), mqtt);
		
		for(Integer id : messangers.keySet()) {
			messangers.get(id).addMessageEventListener(this);
		}
	}
	
	public void stop() {
		
		for(Integer id : messangers.keySet()) {
			messangers.get(id).removeMessageEventListener(this);
		}
		
	}

	public void SendMessage(Message message) {
		messangers.get(message.getId()).send(message.getPayload());
	}
	
	public void subscribe(String topic, int qos, MessageListener subscriber) {
		ArrayList<MessageListener> topicSubscribers = subscriptions.get(topic);
		if(topicSubscribers == null) {
			// No subscribers for the topic yet
			topicSubscribers = new ArrayList<MessageListener>();
		}
		topicSubscribers.add(subscriber);
		subscriptions.put(topic, topicSubscribers);

		// tell messangers to start collecting on the topic
		for(Integer id : messangers.keySet()) {
			messangers.get(id).subscribe(topic, qos);
		}
		log.log(Level.INFO, "Successfully subscribed " +  subscriber.getClass().getSimpleName() + " to topic " + topic);
	}
	
	public void unsubscribe(String topic, MessageListener subscriber) {
		ArrayList<MessageListener> subscribers = subscriptions.get(topic);
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
	
	public void messageEventReceived(MessageEvent event) {
		
		String topic = event.getMessage().getPayload().getTopic();
		ArrayList<MessageListener> subscribers = subscriptions.get(topic);
		if(subscribers == null) {
			// drop message
			return;
		}
		
		log.log(Level.INFO, "Firing new Message to subscribers");
		for(MessageListener subscriber : subscribers) {
			subscriber.onMessageArrived(event.getMessage());
		}
		
	}

}
