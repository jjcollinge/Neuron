package com.thing.api.model;

public interface ObjectMapperStrategy<K, T> {

	public T serialize(K source);
	public K deserialize(T object);
	
}
