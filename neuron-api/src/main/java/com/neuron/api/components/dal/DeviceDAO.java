package com.neuron.api.components.dal;

import java.util.List;

import com.neuron.api.data.Device;
import com.neuron.api.data.GeoPoint;

/**
 * An interface specific for a data access object
 * which can handle {@link com.neuron.api.data.Device}
 * data.
 * @author JC
 *
 */
public interface DeviceDAO extends DAO<Device, Integer> {

	/**
	 * Find devices based on a given geo-location.
	 * Any device within the given range will be
	 * returned.
	 * @param geo
	 * @param range in metres
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByGeo(GeoPoint geo, int range);

	/**
	 * Find devices based on a given sense. 
	 * @param sense
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findBySensorCapability(String sense);

	/**
	 * Find devices based on a given manufacturer
	 * @param manufacturer
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByManufacturer(String manufacturer);

	/**
	 * Find devices based on a given model
	 * @param model
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByModel(String model);

	/**
	 * Find devices based on a given capability
	 * @param capbability
	 * @return list of devices meeting the given criteria
	 */
	public List<Device> findByActuatorCapability(String capbability);

}
