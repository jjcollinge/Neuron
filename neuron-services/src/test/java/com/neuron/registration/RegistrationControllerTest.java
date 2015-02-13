package com.neuron.registration;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.components.dal.DeviceDAOFactory;
import com.neuron.api.data.DatabaseConfiguration;
import com.neuron.dal.mock.MockDeviceDAO;

public class RegistrationControllerTest {

	private DeviceDAOFactory daoFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		daoFactory = new DeviceDAOFactory();
		DatabaseConfiguration config = null;
		try {
			config = new DatabaseConfiguration(
			"127.0.0.1",
			Integer.valueOf("27017"),
			"mongodb",
			"testDatabase",
			Class.forName("com.neuron.dal.MongoDBDeviceDAO"));
			daoFactory.registerDAO(config);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleRequest() {
	
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
		
		//Message requestMsg = new Message(request, "mqtt", "json");
		MockDeviceDAO dao = new MockDeviceDAO();
		//RegistrationWorker worker = new RegistrationWorker(requestMsg, dao, new MockSessionController());
		//new Thread(worker).start();
		//test response & mock all the other bits
	}

	@Test
	public void testHandleResponse() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMaximumNumberOfWorkers() {
		fail("Not yet implemented");
	}

}
