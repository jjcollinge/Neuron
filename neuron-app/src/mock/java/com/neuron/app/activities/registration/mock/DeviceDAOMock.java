package com.neuron.app.activities.registration.mock;

import java.util.List;

import com.neuron.api.data_access.DeviceDAO;
import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;

public class DeviceDAOMock implements DeviceDAO {

	public void initialise(String dbhost, int dbport, String dbname) {
		// TODO Auto-generated method stub
		
	}

	public void insert(Device object) {
		// TODO Auto-generated method stub
		
	}

	public boolean remove(Integer key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(Integer key, String field, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public Device get(Integer key) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> find(String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public List<Device> findByGeo(GeoPoint geo, int range) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> findBySensorCapability(String sense) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> findByManufacturer(String manufacturer) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> findByModel(String model) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> findByActuatorCapability(String capability) {
		// TODO Auto-generated method stub
		return null;
	}

}
