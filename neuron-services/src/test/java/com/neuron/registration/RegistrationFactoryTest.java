package com.neuron.registration;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.data.Actuator;
import com.neuron.api.data.Device;
import com.neuron.api.data.Sensor;

public class RegistrationFactoryTest {

	private static RegistrationFactory factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = new RegistrationFactory();		
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
	public void testGetRegistration() {
		
		final String formatType = "JSON";
		final String request = 
				"{"
				+ "\"returnAddress\": 2020,"
				+ "\"device\": "
				+ "{"
				+ "   \"manufacturer\": \"RaspberryPi\","
				+ "    \"model\": \"B+\","
				+ "    \"geo\": {"
				+ "        \"latitude\": 53.37831623,"
				+ "        \"longitude\": -1.4618752"
				+ "    },"
				+ "    \"sensors\": ["
				+ "        {"
				+ "            \"name\": \"Temperature sensor\","
				+ "            \"sense\": \"temperature\","
				+ "            \"unit\": \"celcius\","
				+ "            \"type\": \"float\""
				+ "        }"
				+ "    ],"
				+ "    \"actuators\": ["
				+ "        {"
				+ "            \"name\": \"LED light bulb\","
				+ "            \"options\": [\"ON\", \"OFF\"]"
				+ "        }"
				+ "    ]"
				+ "}"
				+ "}";
		
		Registration registration = factory.getRegistration(formatType, request);
		Device device = registration.getDevice();
		String returnAddress = registration.getRegistrationAddress();
		assertTrue(returnAddress.equals("2020"));
		assertNotNull(device);
	}
	
	@Test
	public void testGetInvalidRegistration() {
		
		final String formatType = "JSON";
		final String request = 
				"{"
				+ "\"returnAddress\": 2020,"
				+ "\"device\": "
				+ "{"
				+ "   \"manufacturer\": \"RaspberryPi\","
				+ "    \"model\": \"B+\","
				+ "    \"geo\": {"
				+ "        \"laSpatude\": 53.37831623"
				+ "    },"
				+ "    \"sensors\": ["
				+ "        {"
				+ "            \"name\": \"Temperature sensor\","
				+ "            \"sense\": \"temperature\","
				+ "            \"unit\": \"celcius\","
				+ "            \"type\": \"float\""
				+ "    \"actuators\": ["
				+ "        {"
				+ "            \"name\": \"LED light bulb\","
				+ "            \"options\": [\"ON\", \"OFF\"]"
				+ "        }"
				+ "    ]"
				+ "}"
				+ "}";
		
		Registration registration = factory.getRegistration(formatType, request);
		assertNull(registration);
	}
	
	@Test
	public void testGetInCompleteRegistration() {
		
		final String formatType = "JSON";
		final String request = 
				"{"
				+ "\"returnAddress\": 2020,"
				+ "\"device\": "
				+ "{"
				+ "    \"geo\": {"
				+ "        \"laSpatude\": 53.37831623"
				+ "    },"
				+ "    \"sensors\": ["
				+ "        {"
				+ "            \"name\": \"Temperature sensor\","
				+ "            \"type\": \"float\""
				+ "    \"actuators\": ["
				+ "        {"
				+ "            \"name\": \"LED light bulb\","
				+ "            \"options\": [\"ON\", \"OFF\"]"
				+ "        }"
				+ "    ]"
				+ "}"
				+ "}";
		
		Registration registration = factory.getRegistration(formatType, request);
		assertNull(registration);
	}
	
	@Test
	public void testGetRegistrationWithoutReturnAddress() {
		
		final String formatType = "JSON";
		final String request = 
				"{"
				+ "\"device\": "
				+ "{"
				+ "   \"manufacturer\": \"RaspberryPi\","
				+ "    \"model\": \"B+\","
				+ "    \"geo\": {"
				+ "        \"laSpatude\": 53.37831623"
				+ "    },"
				+ "    \"sensors\": ["
				+ "        {"
				+ "            \"name\": \"Temperature sensor\","
				+ "            \"sense\": \"temperature\","
				+ "            \"unit\": \"celcius\","
				+ "            \"type\": \"float\""
				+ "    \"actuators\": ["
				+ "        {"
				+ "            \"name\": \"LED light bulb\","
				+ "            \"options\": [\"ON\", \"OFF\"]"
				+ "        }"
				+ "    ]"
				+ "}"
				+ "}";
		
		Registration registration = factory.getRegistration(formatType, request);
		assertNull(registration);
	}
}
