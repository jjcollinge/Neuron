package com.neuron.dal.mock;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.data.Device;
import com.neuron.api.data.GeoPoint;
import com.neuron.dal.MongoDBDeviceDAO;


public class MockDeviceDAO implements DeviceDAO {

	private static final Logger log = Logger.getLogger(MongoDBDeviceDAO.class
			.getName());
	
	public void initialise(String dbhost, int dbport, String dbname) {
		// TODO Auto-generated method stub
		
	}

	public void insert(Device object) {
		log.log(Level.INFO, "inserted new document");
	}

	public boolean remove(Integer key) {
		log.log(Level.INFO, "remove a document");
		return true;
	}

	public boolean update(Integer key, String field, Object value) {
		log.log(Level.INFO, "updated a document");
		return true;
	}

	public Device get(Integer key) {
		return new Device();
	}

	public List<Device> find(String field, String value) {	
		return new ArrayList<Device>();
	}

	public List<Device> getAll() {
		return new ArrayList<Device>();
	}

	public void clear() {
		
	}

	public List<Device> findByGeo(GeoPoint geo, int range) {
		return new ArrayList<Device>();
	}

	public List<Device> findBySensorCapability(String sense) {
		return new ArrayList<Device>();
	}

	public List<Device> findByManufacturer(String manufacturer) {
		return new ArrayList<Device>();
	}

	public List<Device> findByModel(String model) {
		return new ArrayList<Device>();
	}

	public List<Device> findByActuatorCapability(String capability) {
		return new ArrayList<Device>();
	}

}
