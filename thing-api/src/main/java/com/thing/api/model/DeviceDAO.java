package com.thing.api.model;

import java.util.List;

public interface DeviceDAO extends DAO<Device, Integer> {

	public List<Device> findByGeo(GeoPoint geo, int range);
	public List<Device> findBySensorCapability(String sense);
	public List<Device> findByManufacturer(String manufacturer);
	public List<Device> findByModel(String model);
	public List<Device> findByActuatorCapability(String capbability);
	
}
