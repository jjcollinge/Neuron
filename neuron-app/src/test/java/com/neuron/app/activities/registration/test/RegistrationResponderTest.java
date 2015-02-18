package com.neuron.app.activities.registration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.configuration.ProtocolConfiguration;
import com.neuron.api.model.Device;
import com.neuron.api.response.Response;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationResponder;
import com.neuron.app.mocks.MqttAdapterMock;

public class RegistrationResponderTest {

	public static RegistrationResponder responder;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		responder = new RegistrationResponder();
		AdapterFactory factory = AdapterFactory.getFactory();
		factory.registerAdapter(new ProtocolConfiguration("", 0, "mock", MqttAdapterMock.class));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleRegistrationResponse() {
		
		// Given
		Registration registration = new Registration();
		Device device = new Device();
		registration.setDevice(device);
		registration.setRegistrationAddress("1773");
		registration.addProperty("format", "json");
		registration.addProperty("protocol", "mock");
		registration.addProperty("status", "200");
		registration.addProperty("id", "2");
		
		// When
		responder.onRegistration(registration);
		
		// Should
		Response actual = new MqttAdapterMock().getLastResponse();
		assertEquals("json", actual.getFormats().get(0));
		assertEquals("mock", actual.getProtocols().get(0));
		assertEquals("2", actual.getPayload().getPayload().toString());
		assertEquals("1773", actual.getHeader("topic"));
		
	}
	
	@Test
	public void testHandleBadRegistrationResponse() {
		
		// Given
		Registration registration = new Registration();
		Device device = new Device();
		registration.setDevice(device);
		registration.addProperty("status", "400");
		
		// When
		responder.onRegistration(registration);
		
		Response actual = new MqttAdapterMock().getLastResponse();
		
		// Should
		assertNull(actual);
		
	}

}
