package com.neuron.api.components.dal;

import com.neuron.api.data.Device;

/**
 * A device specific implementation of an object mapper.
 * Is used to serialize or deserialize a device from a
 * format.
 * @author JC
 *
 * @param <T> The format type from which the device it mapping to
 */
public class DeviceMapper<T> implements ObjectMapper<Device, T> {

	@SuppressWarnings("rawtypes")
	private ObjectMapperStrategy mapper;

	/**
	 * Inject a mapping strategy for a particular format type
	 */
	@SuppressWarnings("rawtypes")
	public void setMapperStrategy(ObjectMapperStrategy mapper) {
		this.mapper = mapper;
	}

	/**
	 * Serialize a device into a format type based on the mapping strategy
	 */
	@SuppressWarnings("unchecked")
	public T serialize(Device obj) {
		return (T) mapper.serialize(obj);
	}

	/**
	 * Deserialize a format type into a device based on the mapping strategy
	 */
	@SuppressWarnings("unchecked")
	public Device deserialize(T source) {
		return (Device) mapper.deserialize(source);
	}
}
