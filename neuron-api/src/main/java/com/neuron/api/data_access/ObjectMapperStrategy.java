package com.neuron.api.data_access;

/**
 * A generic interface for mapper strategies
 * @author JC
 *
 * @param <K> serial type
 * @param <T> Object type
 */
public interface ObjectMapperStrategy<K, T> {

	public T serialize(K source);
	public K deserialize(T object);
	
}
