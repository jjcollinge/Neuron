package com.thing.api.model;

public class SessionMapper<T> implements ObjectMapper<Session, T> {
	
	private ObjectMapperStrategy mapper;
	
	public void setMapperStrategy(ObjectMapperStrategy mapper) {
		this.mapper = mapper;
	}

	public T serialize(Session obj) {
		return (T) mapper.serialize(obj);
	}
	
	public Session deserialize(T source) {
		return (Session) mapper.deserialize(source);
	}

}
