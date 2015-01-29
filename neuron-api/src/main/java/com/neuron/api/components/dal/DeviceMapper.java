package com.neuron.api.components.dal;

import com.neuron.api.data.Device;

public class DeviceMapper<T> implements ObjectMapper<Device, T>{

	private ObjectMapperStrategy mapper;
	
	public void setMapperStrategy(ObjectMapperStrategy mapper) {
		this.mapper = mapper;
	}

	public T serialize(Device obj) {
		return (T) mapper.serialize(obj);
	}
	
	public Device deserialize(T source) {
		return (Device) mapper.deserialize(source);
	}
}
