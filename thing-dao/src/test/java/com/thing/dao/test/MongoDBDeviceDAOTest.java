package com.thing.dao.test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.thing.api.model.Device;
import com.thing.api.model.GeoPoint;
import com.thing.api.model.Sensor;
import com.thing.storage.MongoDBDeviceDAO;


public class MongoDBDeviceDAOTest {

	@Test
	public void testInsertAndRetrieveDevice() {
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		testSensor.setValue("test");
		
		testDevice.addSensor(testSensor);
		
		dao.insert(testDevice);
		Device returnedDevice = dao.get(0);
		
		assertEquals("Device ids should be equal", testDevice.getId(), returnedDevice.getId());
		
		dao.clear();
	}
	
	@Test
	public void testInsertRemoveAndRetrieveDevice() {
		MongoDBDeviceDAO dao = new MongoDBDeviceDAO();
		dao.setCollection("testDevices");
		dao.clear();
		
		Device testDevice = new Device();
		testDevice.setId(0);
		testDevice.setModel("test");
		testDevice.setManufacurer("test");
		testDevice.setGeo(10.0, 10.0);
		
		Sensor testSensor = new Sensor();
		testSensor.setId(0);
		testSensor.setSense("test");
		testSensor.setType("test");
		testSensor.setUnit("test");
		testSensor.setValue("test");
		
		testDevice.addSensor(testSensor);
		
		dao.insert(testDevice);
		assertTrue("Device should get deleted", dao.remove(testDevice.getId()));
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
			testDevice.setId(i);
			testDevice.setManufacurer(manufacturer);
			dao.insert(testDevice);
		}
		
		Device dummyDevice = new Device();
		dummyDevice.setId(numberOfDevice + 1);
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
		
		int distanceInMiles = 20;
		
		GeoPoint point = new GeoPoint(51.326096, -0.101213);
		
		Device goodDevice = new Device();
		goodDevice.setId(1);
		goodDevice.setGeo(51.329072, -0.109796);
		
		Device badDevice = new Device();
		badDevice.setId(2);
		badDevice.setGeo(53.208284, -2.166643);

		dao.insert(goodDevice);
		dao.insert(badDevice);
		
		ArrayList<Device> devicesFound = (ArrayList<Device>) dao.findByGeo(point, distanceInMiles);
		assertTrue("Should be one device in range, found: " + devicesFound.size(), devicesFound.size() == 1);
		assertTrue("Device in range id should be 1", devicesFound.get(0).getId() == 1);
		
		dao.clear();
	}
}
