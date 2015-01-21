package com.thing.registration.test;

import org.junit.Test;

import com.thing.api.messaging.Message;
import com.thing.registration.RegistrationController;

public class RegistrationWorkFlowTest {

	@Test
	public void testValidRegistrationRequest() {
		
		RegistrationController controller = new RegistrationController();
		String payload = "{\"returnAddress\":\"5680\",\"device\":{ " +
						    "\"manufacturer\": \"RaspberryPi\", " +
						    "\"model\": \"B+\", " +
						    "\"geo\": {" +
						        "\"latitude\": 23.1," +
						        "\"longitude\": 34.1" +
						    "}," +
						    "\"sensors\": [" +
						        "{" +
						            "\"name\": \"Temperature sensor\"," +
						            "\"sense\": \"temperature\"," +
						            "\"unit\": \"celcius\"," +
						            "\"type\": \"float\"," +
						            "\"value\": \"0.0\"" +
						        "}" +
						   " ]," +
						    "\"actuators\": [" +
						        "{" +
						            "\"name\": \"LED light bulb\"," +
						            "\"options\": [\"ON\", \"OFF\"]" +
						        "}" +
						    "]" +
						"}}";
		Message request = new Message(payload, "JSON", "MQTT");
		controller.handleRequest(request);
		
		
	}

}
