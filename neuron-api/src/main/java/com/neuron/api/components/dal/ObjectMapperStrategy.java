package com.neuron.api.components.dal;

public interface ObjectMapperStrategy<K, T> {

	public T serialize(K source);
	public K deserialize(T object);
	
}
