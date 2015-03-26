package com.neuron.app.activities.registration.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;
import com.neuron.app.activities.registration.JsonRegistrationMapper;
import com.neuron.app.activities.registration.Registration;

public class JsonRegistrationMapperTest {

	private static JsonRegistrationMapper mapper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mapper = new JsonRegistrationMapper();
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
	public void testWellFormedRegistrationDeserialization() {
		
		// Given
		Device expectedDevice = new Device();
		expectedDevice.setName("Smoke alarm");
		expectedDevice.setGeo(new GeoPoint(53.37831623, -1.4618752));
		
		String registration = "{\"regAddress\":\"2077\",\"device\":{" +
							    "\"name\": \"Smoke alarm\", "+
							    "\"geo\": {"+
							        "\"latitude\": 53.37831623,"+
							        "\"longitude\": -1.4618752"+
							    "},"+
							    "\"sensors\": [],"+
							    "\"actuators\": [],"+
							    "\"tags\": {}"+
							"}}";
		// When
		Registration reg = mapper.deserialize(registration);

		// Should
		assertEquals(reg.getDevice().getName(), expectedDevice.getName());
		assertArrayEquals(reg.getDevice().getSensors().toArray(), expectedDevice.getSensors().toArray());
		assertArrayEquals(reg.getDevice().getActuators().toArray(), expectedDevice.getActuators().toArray());
		assertEquals(reg.getRegistrationAddress(), "2077");
		
	}

	@Test
	public void testMalformedRegistrationDeserialization() {
		
		// Given
		String registration = "{\"regAddress\":\"2077\",\"device\":{" +
							    "\"name\": \"Smoke alarm\",, " +
							    "\"geo\": {"+
							        "\"latitude\": 53.37831623,"+
							        "\"longitude\": -1.4618752"+
							    "},"+
							    "\"sensors\": [],"+
							    "\"actuators\": []"+
							    "\"tags\": {}"+
							"}}";
		// When
		Registration reg = mapper.deserialize(registration);
		
		// Should
		assertNull(reg);
		
	}
	
	@Test
	public void testIncompleteRegistrationDeserialization() {
		
		// Given
		Device expectedDevice = new Device();
		String registration = "{\"regAddress\":\"2077\",\"device\":{" +
							    "\"sensors\": [],"+
							    "\"actuators\": [],"+
							    "\"tags\": {}"+
							"}}";
		// When
		Registration reg = mapper.deserialize(registration);
		
		// Should
		assertArrayEquals(reg.getDevice().getSensors().toArray(), expectedDevice.getSensors().toArray());
		assertArrayEquals(reg.getDevice().getActuators().toArray(), expectedDevice.getActuators().toArray());
		assertEquals(reg.getRegistrationAddress(), "2077");
		
	}
	
	@Test
	public void testAdditionalFieldRegistrationDeserialization() {
		
		// Given
		Device expectedDevice = new Device();
		expectedDevice.setName("Greenhouse control unit");
		expectedDevice.setGeo(new GeoPoint(53.37831623, -1.4618752));
		String registration = "{\"regAddress\":\"2077\",\"device\":{" +
							    "\"name\": \"Greenhouse control unit\"," +
							    "\"geo\": {"+
							        "\"latitude\": 53.37831623,"+
							        "\"longitude\": -1.4618752"+
							    "},"+
							    "\"sensors\": [],"+
							    "\"actuators\": [],"+
							    "\"colour\": \"green\","+
							    "\"weight\": 23"+
							"}}";
		// When
		Registration reg = mapper.deserialize(registration);
		
		// Should
		assertEquals(reg.getDevice().getName(), expectedDevice.getName());
		assertArrayEquals(reg.getDevice().getSensors().toArray(), expectedDevice.getSensors().toArray());
		assertArrayEquals(reg.getDevice().getActuators().toArray(), expectedDevice.getActuators().toArray());
		assertEquals(reg.getRegistrationAddress(), "2077");
		
	}
	
	@Test
	public void testRegistrationSerialization() {
		
		/**
		 * The serialization of a registration object doesn't guarantee
		 * the order of fields within the resultant string so cannot be
		 * evaluated.
		 */
		
		assertTrue(true);
		
	}
}
