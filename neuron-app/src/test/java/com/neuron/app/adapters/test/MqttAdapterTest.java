package com.neuron.app.adapters.test;

import static org.junit.Assert.*;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.model.Payload;
import com.neuron.api.response.Response;
import com.neuron.app.adapters.MqttAdapter;
import com.neuron.app.adapters.mock.MqttAsyncClientMock;

public class MqttAdapterTest {

	private static MqttAdapter adapter;
	private static MqttAsyncClientMock client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			client = new MqttAsyncClientMock("tcp://localhost:1883", MqttClient.generateClientId());
		} catch (MqttException e) {
			e.printStackTrace();
		}
		adapter = new MqttAdapter();
		adapter.connect(client);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		client.resetLastMessage();
	}

	@Test
	public void testSendingMessage() {
		
		// Given
		Payload payload = new Payload("hello world");
		Response response = new Response(payload);
		response.addFormat("json");
		response.addHeader("topic", "2222");
		response.addProtocol("mqtt");
		
		adapter.send(response);
		
		String actual = client.getLastMessage();
		assertEquals("{\"payload\":\"hello world\"}", actual);
		
	}
	
	@Test
	public void testSendingMessageWithoutTopic() {
		
		// Given
		Payload payload = new Payload("hello world");
		Response response = new Response(payload);
		response.addFormat("json");
		response.addProtocol("mqtt");
		
		adapter.send(response);

		String actual = client.getLastMessage();
		assertNull(actual);
		
	}
	
	@Test
	public void testSendingMessageWithoutFormat() {
		
		// Given
		Payload payload = new Payload("hello world");
		Response response = new Response(payload);
		response.addHeader("topic", "2222");
		response.addProtocol("mqtt");
		
		adapter.send(response);

		String actual = client.getLastMessage();
		assertNull(actual);
		
	}

}
