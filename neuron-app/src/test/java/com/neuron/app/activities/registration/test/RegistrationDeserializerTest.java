package com.neuron.app.activities.registration.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;
import com.neuron.app.activities.registration.JsonRegistrationMapper;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.registration.RegistrationDeserializer;

public class RegistrationDeserializerTest {

	private static RegistrationDeserializer deserializer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		deserializer = new RegistrationDeserializer();
		deserializer.addFormat("json", new JsonRegistrationMapper());
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
	public void testDeserializingRegistration() {
		
		// Given
		Device expectedDevice = new Device();
		expectedDevice.setName("Greenhouse control unit");
		expectedDevice.setGeo(new GeoPoint(53.37831623, -1.4618752));
		String registration = "{\"regAddress\":\"2077\","+
								"\"device\":{" +
								    "\"name\": \"Greenhouse control unit\","+
								    "\"geo\": {"+
								        "\"latitude\": 53.37831623,"+
								        "\"longitude\": -1.4618752"+
								    "},"+
								    "\"sensors\": [],"+
								    "\"actuators\": [],"+
								    "\"tags\": {}"+
								 "}"+
							  "}";
		// When
		Registration reg = deserializer.deserialize(registration);
		
		// Should
		assertEquals(reg.getDevice().getName(), expectedDevice.getName());
		assertArrayEquals(reg.getDevice().getSensors().toArray(), expectedDevice.getSensors().toArray());
		assertArrayEquals(reg.getDevice().getActuators().toArray(), expectedDevice.getActuators().toArray());
		assertEquals(reg.getRegistrationAddress(), "2077");
		
	}
	
	@Test
	public void testDeserializingUnsupportedFormatRegistration() {
		
		// Given
		String registration = "<[regAddress = 2077],[device, [" +
							    "[manufacturer = RaspberryPi]," +
							    "[model = B+]," +
							    "[geo, [" +
							        "latitude = 53.37831623" +
							        "longitude = -1.4618752" +
							    "]],"+
							    "[sensors, []],"+
							    "[actuators, []]"+
							"]>";
		// When
		Registration reg = deserializer.deserialize(registration);
		
		// Should
		assertNull(reg);
		
	}

}
