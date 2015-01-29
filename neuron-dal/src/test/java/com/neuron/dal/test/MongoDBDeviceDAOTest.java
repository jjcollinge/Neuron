package com.neuron.dal.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.neuron.api.data.Actuator;
import com.neuron.api.data.Device;
import com.neuron.api.data.GeoPoint;
import com.neuron.api.data.Sensor;
import com.neuron.dal.MongoDBDeviceDAO;


public class MongoDBDeviceDAOTest {

	@Test
	public void testInsertAndRetrieveDevice() {
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		
		Actuator testActuator = new Actuator();
		testActuator.setId(0);
		testActuator.setName("test");
		testActuator.addOption("OPEN");
		testActuator.addOption("CLOSE");
		
		testDevice.addActuator(testActuator);
		testDevice.addSensor(testSensor);
		
		dao.insert(testDevice);
		Device returnedDevice = dao.get(0);
		
		assertEquals("Device ids should be equal", testDevice.getSessionId(), returnedDevice.getSessionId());
		
		dao.clear();
	}
	
	@Test
	public void testInsertRemoveAndRetrieveDevice() {
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		
		testDevice.addSensor(testSensor);
		
		dao.insert(testDevice);
		assertTrue("Device should get deleted", dao.remove(testDevice.getSessionId()));
		Device returnedDevice = dao.get(0);
		
		assertNull("Device should be null", returnedDevice);
		
		dao.clear();
	}
	
	@Test
	public void testRemoveAndRetrieveNonExistentDevice() {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		assertFalse("Device shouldn't be deleted", dao.remove(1337));
		Device returnedDevice = dao.get(1337);
		
		assertNull("Device should be null", returnedDevice);
		
	}
	
	@Test
	public void testFindDeviceByQuery() {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		String manufacturer = "testManufacturer";
		int numberOfDevice = 10;
		
		for(int i = 0; i < numberOfDevice; i++) {
			Device testDevice = new Device();
			testDevice.setSessionId(i);
			testDevice.setManufacurer(manufacturer);
			dao.insert(testDevice);
		}
		
		Device dummyDevice = new Device();
		dummyDevice.setSessionId(numberOfDevice + 1);
		dummyDevice.setManufacurer("redHerringManufacturer");
		
		ArrayList<Device> devicesFound = (ArrayList<Device>) dao.findByManufacturer(manufacturer);
		assertTrue(devicesFound.size() == numberOfDevice);
		
		for(Device device :  devicesFound) {
			assertTrue(device.getManufacturer().equals(manufacturer));
		}
		
		dao.clear();
	}
	
	@Test
	public void testFindDeviceByGeo() {
		
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

		dao.insert(goodDevice);
		dao.insert(badDevice);
		
		ArrayList<Device> devicesFound = (ArrayList<Device>) dao.findByGeo(point, metersProximity);
		assertTrue("Should be one device in range, found: " + devicesFound.size(), devicesFound.size() == 1);
		assertTrue("Device in range id should be 1", devicesFound.get(0).getSessionId() == 1);
		
		dao.clear();
	}
	
	@Test
	public void testUpdateDevice() {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		
		testDevice.addSensor(testSensor);	
		dao.insert(testDevice);
		assertTrue(dao.update(0, "model", "Rhino"));
		Device result = dao.get(0);
		assertEquals(result.getModel(), "Rhino");
		
	}
	
	@Test
	public void testUpdateOnUnknownField() {
		
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setSessionId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		
		testDevice.addSensor(testSensor);	
		dao.insert(testDevice);
		assertFalse(dao.update(0, "colour", "blue"));
		
	}
	
}
