package com.neuron.api.components.dal;

import java.util.List;

import com.neuron.api.data.Device;
import com.neuron.api.data.GeoPoint;

public interface DeviceDAO extends DAO<Device, Integer> {

	public List<Device> findByGeo(GeoPoint geo, int range);
	public List<Device> findBySensorCapability(String sense);
	public List<Device> findByManufacturer(String manufacturer);
	public List<Device> findByModel(String model);
	public List<Device> findByActuatorCapability(String capbability);
	
}
