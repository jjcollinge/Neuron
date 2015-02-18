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

public class DevicesResourceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
		expect().statusCode(200)
				.body("message", equalTo("Hello World"))
				.when()
				.get("http://localhost:9998/api/devices/hello");
	}
	
	@Test
	public void testDevicesFetch() {
		
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
		
		expect()
			.body("[0].manufacturer", equalTo("RaspberryPi"))
			.body("[0].model", equalTo("B+"))
			.body("[0].sessionId", equalTo(0))
			.when()
			.get("http://localhost:9998/api/devices");
		
	}

}
