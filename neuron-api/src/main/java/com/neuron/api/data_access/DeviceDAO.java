package com.neuron.api.data_access;

import java.util.List;

import com.neuron.api.model.Device;
import com.neuron.api.model.GeoPoint;

/**
 * An interface specific for a data access object
 * which can handle @link com.neuron.api.data.Device
 * data.
 * @author JC
 *
 */
public interface DeviceDAO extends DAO<Device, Integer> {

	/**
	 * Find devices based on a given geo-location.
	 * Any device within the given range will be
	 * returned.
	 * @param geo The geolocation of the device
	 * @param range The distance threshold from the given point
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByGeo(GeoPoint geo, int range);

	/**
	 * Find devices based on a given sense. 
	 * @param sense Sensory capability
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findBySensorCapability(String sense);

	/**
	 * Find devices based on a given manufacturer
	 * @param manufacturer Device manufacturer
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByName(String manufacturer);

	/**
	 * Find devices based on a given model
	 * @param model Device model
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByModel(String model);

	/**
	 * Find devices based on a given capability
	 * @param capability Actuator capability
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByActuatorCapability(String capability);

}
