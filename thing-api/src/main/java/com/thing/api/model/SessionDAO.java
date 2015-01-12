package com.thing.api.model;

import java.util.List;

public interface SessionDAO extends DAO<Session, Integer> {

	public List<Session> findByTimestamp(long timestamp);
	public List<Session> findByProtocol(String protocol);
	public List<Session> findByFormat(String format);
	
}
