package com.neuron.app.dal.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.neuron.api.model.Actuator;
import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;
import com.neuron.api.model.Sensor;
import com.neuron.app.dal.MongoDBDeviceDAO;


public class MongoDBDeviceDAOTest {

	@Test
	public void testInsertAndRetrieveDevice() {
		
		//Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setName("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setUnit("test");
		
		Actuator testActuator = new Actuator();
		testActuator.setId(0);
		testActuator.setDesc("test");
		testActuator.addOption("OPEN");
		testActuator.addOption("CLOSE");
		
		testDevice.addActuator(testActuator);
		testDevice.addSensor(testSensor);
		
		// When
		dao.insert(testDevice);
		Device returnedDevice = dao.get(0);
		
		// Should
		assertEquals("Device ids should be equal", testDevice.getSessionId(), returnedDevice.getSessionId());
		
		dao.clear();
	}
	
	@Test
	public void testInsertRemoveAndRetrieveDevice() {
		
		//Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setName("test");
		
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setUnit("test");
		
		testDevice.addSensor(testSensor);
		
		// When
		dao.insert(testDevice);
		
		// Should
		assertTrue("Device should get deleted", dao.remove(testDevice.getSessionId()));
		
		// When
		Device returnedDevice = dao.get(0);
		
		// Should
		assertNull("Device should be null", returnedDevice);
		
		dao.clear();
	}
	
	@Test
	public void testRemoveAndRetrieveNonExistentDevice() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		
		// When
		dao.setCollection("testDevices");
		dao.clear();
		
		// Should
		assertFalse("Device shouldn't be deleted", dao.remove(1337));
		
		// When
		Device returnedDevice = dao.get(1337);
		
		// Should
		assertNull("Device should be null", returnedDevice);
		
	}
	
	@Test
	public void testFindDeviceByQuery() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		String name = "testName";
		int numberOfDevice = 10;
		
		// When
		for(int i = 0; i < numberOfDevice; i++) {
			Device testDevice = new Device();
			testDevice.setSessionId(i);
			testDevice.setName(name);
			dao.insert(testDevice);
		}
		
		Device dummyDevice = new Device();
		dummyDevice.setSessionId(numberOfDevice + 1);
		dummyDevice.setName("redHerringName");
		
		// Should
		ArrayList<Device> devicesFound = (ArrayList<Device>) dao.findByName(name);
		assertTrue(devicesFound.size() == numberOfDevice);
		
		for(Device device :  devicesFound) {
			assertTrue(device.getName().equals(name));
		}
		
		dao.clear();
	}
	
	@Test
	public void testFindDeviceWithSensor() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setName("test");
		
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("temperature");
		testSensor.setUnit("test");
		
		Actuator testActuator = new Actuator();
		testActuator.setId(0);
		testActuator.setDesc("test");
		testActuator.addOption("OPEN");
		testActuator.addOption("CLOSE");
		
		testDevice.addActuator(testActuator);
		testDevice.addSensor(testSensor);
		
		// When
		dao.insert(testDevice);
		ArrayList<Device> returnedDevices = (ArrayList<Device>) dao.findBySensorCapability("temperature");

		// Should
		assertEquals("Device ids should be equal", testDevice.getSessionId(), returnedDevices.get(0).getSessionId());
		
		dao.clear();
	}
	
	@Test
	public void testFindDeviceByGeo() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		int metersProximity = 200;
		
		GeoPoint point = new GeoPoint(53.37831623, -1.4618752);
		
		Device goodDevice = new Device();
		goodDevice.setSessionId(1);
		goodDevice.setGeo(53.37831303, -1.46192884);
		
		Device badDevice = new Device();
		badDevice.setSessionId(2);
		badDevice.setGeo(53.208284, -2.166643);

		// When
		dao.insert(goodDevice);
		dao.insert(badDevice);
		
		// Should
		ArrayList<Device> devicesFound = (ArrayList<Device>) dao.findByGeo(point, metersProximity);
		assertTrue("Should be one device in range, found: " + devicesFound.size(), devicesFound.size() == 1);
		assertTrue("Device in range id should be 1", devicesFound.get(0).getSessionId() == 1);
		
		dao.clear();
	}
	
	@Test
	public void testUpdateDevice() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setName("test");
		
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setUnit("test");
		
		// When
		testDevice.addSensor(testSensor);	
		dao.insert(testDevice);
		
		// Should
		assertTrue(dao.update(0, "name", "Rhino"));
		
		// When
		Device result = dao.get(0);
		
		// Should
		assertEquals(result.getName(), "Rhino");
		
	}
	
	@Test
	public void testUpdateOnUnknownField() {
		
		// Given
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setName("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setUnit("test");

		testDevice.addSensor(testSensor);
		
		// When
		dao.insert(testDevice);
		
		// Should
		assertFalse(dao.update(0, "colour", "blue"));
		
	}
	
}
