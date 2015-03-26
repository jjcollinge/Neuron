package com.neuron.app.activities.registration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.configuration.Configuration;
import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;
import com.neuron.api.request.Request;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationRequestHandler;
import com.neuron.app.mocks.DeviceDAOMock;
import com.neuron.app.mocks.RegistrationResponderMock;

public class RegistrationRequestHandlerTest {

	static RegistrationRequestHandler handler;
	static Configuration config;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		handler = new RegistrationRequestHandler();
		config = new Configuration();
		config.addProperty("registration_topic", "1773");
		handler.setup(config);
		handler.setDeviceDao(new DeviceDAOMock());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		handler.start();
	}

	@After
	public void tearDown() throws Exception {
		handler.stop();
	}

	@Test
	public void testHandleRegistrationRequest() {
		
		// Given
		Request request = new Request();
		request.setData("{\"regAddress\":\"2077\",\"device\":{" +
					    "\"name\": \"Greenhouse control unit\", " +
					    "\"geo\": {"+
					        "\"latitude\": 53.37831623,"+
					        "\"longitude\": -1.4618752"+
					    "},"+
					    "\"sensors\": [],"+
					    "\"actuators\": [],"+
					    "\"tags\": {}"+
						"}}");
		request.setFormat("json");
		request.setProtocol("mqtt");
		request.addHeader("topic", "1773");
		
		Device expectedDevice = new Device();
		expectedDevice.setName("Greenhouse control unit");
		expectedDevice.setGeo(new GeoPoint(53.37831623, -1.4618752));
		Registration expectedRegistration = new Registration();
		expectedRegistration.setDevice(expectedDevice);
		expectedRegistration.setRegistrationAddress("2077");
		
		RegistrationResponderMock responder = new RegistrationResponderMock();
		
		// When
		handler.addRegistrationListener(responder);
		handler.onRequest(request);

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Should
		assertEquals(expectedRegistration.getRegistrationAddress(), responder.getLastRegistration().getRegistrationAddress());
		assertEquals(expectedRegistration.getDevice().getName(), responder.getLastRegistration().getDevice().getName());
		
		handler.removeRegistrationListener(responder);
	}
	
	@Test
	public void testHandleNonRegistrationRequest() {
		
		// Given
		Request request = new Request();
		request.setData("{\"data\": \"123213.2\"}");
		request.setFormat("json");
		request.setProtocol("mqtt");
		request.addHeader("topic", "1773");
	
		RegistrationResponderMock responder = new RegistrationResponderMock();
		
		// When
		handler.addRegistrationListener(responder);
		handler.onRequest(request);
		
		// Should
		assertNull(responder.getLastRegistration());
	}

	@Test
	public void testEmptyRegistration() {
		
		// Given
		Request request = new Request();
		request.setData("{\"regAddress\":\"\",\"device\":{}");
		request.setFormat("json");
		request.setProtocol("mqtt");
		request.addHeader("topic", "1773");
	
		RegistrationResponderMock responder = new RegistrationResponderMock();
		
		// When
		handler.addRegistrationListener(responder);
		handler.onRequest(request);

		// Should
		assertNull(responder.getLastRegistration());
	}
	
}
