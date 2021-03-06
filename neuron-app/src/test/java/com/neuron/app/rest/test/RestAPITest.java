package com.neuron.app.rest.test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.adapters.Adapter;
import com.neuron.api.adapters.AdapterFactory;
import com.neuron.api.configuration.ProtocolConfiguration;
import com.neuron.api.model.Payload;
import com.neuron.api.response.Response;

/**
 * Requires the system to be running to test the API
 * @author JC
 *
 */

public class RestAPITest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		String registration = "{\"regAddress\":\"4406\",\"device\":{" +
			    "\"manufacturer\": \"RaspberryPi\"," +
			    "\"model\": \"B+\"," +
			    "\"geo\": {" +
			        "\"latitude\": 53.37831623," +
			        "\"longitude\": -1.4618752" +
			    "}," +
			    "\"sensors\": [" +
			        "{" +
			            "\"name\": \"Temperature sensor\"," +
			            "\"sense\": \"temperature\"," +
			            "\"unit\": \"celcius\"," +
			            "\"type\": \"float\"" +
			        "}" +
			    "]," +
			    "\"actuators\": [" +
			        "{" +
			            "\"name\": \"LED light bulb\"," +
			            "\"options\": [\"ON\", \"OFF\"]" +
			        "}" +
			    "]" +
			"}}";
			
			AdapterFactory factory = AdapterFactory.getFactory();
			factory.registerAdapter(new ProtocolConfiguration(
										"localhost",
										1883,
										"mqtt",
										com.neuron.app.adapters.MqttAdapter.class));
			Adapter adapter = AdapterFactory.getFactory().getAdapter("mqtt");
			Payload payload = new Payload(registration);
			Response res = new Response(payload);
			res.addHeader("topic", "register");
			res.addProtocol("mqtt");
			res.addFormat("json");
			res.addHeader("raw", "true");
			adapter.send(res);
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
	public void testHelloWorld() {	
		
//		expect().statusCode(200)
//				.body("message", equalTo("Hello World"))
//				.when()
//				.get("http://localhost:9998/api/devices/hello");
	}
	
	@Test
	public void testDevicesFetch() {

//		expect()
//			.body("[0].manufacturer", equalTo("RaspberryPi"))
//			.body("[0].model", equalTo("B+"))
//			.body("[0].sessionId", equalTo(0))
//			.when()
//			.get("http://localhost:9998/api/devices");
	}
	
	@Test
	public void testDeviceFetch() {
			
//		expect()
//			.body("manufacturer", equalTo("RaspberryPi"))
//			.body("model", equalTo("B+"))
//			.body("sessionId", equalTo(0))
//			.when()
//			.get("http://localhost:9998/api/devices/0");
	}
	
	@Test
	public void testSensorsFetch() {
		
//		expect()
//		.body("sensors[0].id", equalTo(0))
//		.body("sensors[0].name", equalTo("Temperature sensor"))
//		.when()
//		.get("http://localhost:9998/api/devices/0/sensors");
	}
	
	@Test
	public void testActuatorsFetch() {
		
//		expect()
//		.body("actuator[0].id", equalTo(0))
//		.body("actuator[0].name", equalTo("LED light bulb"))
//		.when()
//		.get("http://localhost:9998/api/devices/0/actuators");
	}

}
