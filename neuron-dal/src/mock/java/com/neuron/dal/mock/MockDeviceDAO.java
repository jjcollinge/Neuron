package com.neuron.dal.mock;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.neuron.api.components.dal.DeviceDAO;
import com.neuron.api.data.Device;
import com.neuron.api.data.GeoPoint;


public class MockDeviceDAO implements DeviceDAO {

	private static final Logger log = Logger.getLogger(MockDeviceDAO.class
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
		return null;
	}

	public List<Device> find(String field, String value) {	
		return null;
	}

	public List<Device> getAll() {
		return null;
	}

	public void clear() {
		
	}

	public List<Device> findByGeo(GeoPoint geo, int range) {
		return null;
	}

	public List<Device> findBySensorCapability(String sense) {
		return null;
	}

	public List<Device> findByManufacturer(String manufacturer) {
		return null;
	}

	public List<Device> findByModel(String model) {
		return null;
	}

	public List<Device> findByActuatorCapability(String capability) {
		return null;
	}

}
