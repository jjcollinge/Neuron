package com.thing.api.model;

public interface ObjectMapper<K, T> {

	public void setMapperStrategy(ObjectMapperStrategy mapper);
	public T serialize(K obj);
	public K deserialize(T source);
	
}
