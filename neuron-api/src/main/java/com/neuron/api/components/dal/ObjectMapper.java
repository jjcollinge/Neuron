package com.neuron.api.components.dal;

public interface ObjectMapper<K, T> {

	public void setMapperStrategy(ObjectMapperStrategy mapper);
	public T serialize(K obj);
	public K deserialize(T source);
	
}
