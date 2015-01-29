package com.neuron.api.components.dal;

/**
 * Top level interface for any object
 * mappers. Provides basic expected
 * API for a mapper.
 * @author JC
 *
 * @param <K> POJO type
 * @param <T> serial type
 */
public interface ObjectMapper<K, T> {

	public void setMapperStrategy(@SuppressWarnings("rawtypes") ObjectMapperStrategy mapper);
	public T serialize(K obj);
	public K deserialize(T source);
	
}
