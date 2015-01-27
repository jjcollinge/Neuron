package com.thing.api.model;

import java.util.List;

public interface DAO<T, K> {
	
	public void initialise(String dbhost, String dbname);
	public void insert(T object);
	public boolean remove(K key);
	public boolean update(K key, String field, Object value);
	public T get(K key);
	public List<T> find(String field, String value);
	public List<T> getAll();
	public void clear();
	
}
