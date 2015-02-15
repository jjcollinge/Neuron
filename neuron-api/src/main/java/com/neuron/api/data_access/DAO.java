package com.neuron.api.data_access;

import java.util.List;

/**
 * An interface to provide the minimum CRUD
 * requirements of any DAO implementation.
 * @author JC
 *
 * @param <T> Object type
 * @param <K> Key type
 */
public interface DAO<T, K> {
	
	public void initialise(String dbhost, int dbport, String dbname);
	public void insert(T object);
	public boolean remove(K key);
	public boolean update(K key, String field, Object value);
	public T get(K key);
	public List<T> find(String field, String value);
	public List<T> getAll();
	public void clear();
	
}
