package com.thing.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.thing.api.model.Device;
import com.thing.api.model.Sensor;
import com.thing.api.model.Session;
import com.thing.storage.MongoDBDeviceDAO;
import com.thing.storage.MongoDBSessionDAO;

public class MongoDBSessionDAOTest {

	@Test
	public void testInsertAndRetrieveSession() {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		dao.setCollection("testSessions");
		dao.clear();
	
		Session testSession = new Session(0, "test", "test");
		dao.insert(testSession);
		Session returnedSession = dao.get(0);
		
		assertEquals("Session ids should be equal", testSession.getId(), returnedSession.getId());
	
	}
	
	@Test
	public void testInsertRemoveAndRetrieve() {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		dao.setCollection("testSessions");
		dao.clear();
	
		Session testSession = new Session(0, "test", "test");
		dao.insert(testSession);
		assertTrue("Session should be deleted", dao.remove(testSession.getId()));
		Session returnedSession = dao.get(0);
		
		assertNull("Session should be null", returnedSession);

	}
	
	@Test
	public void testRemoveAndRetrieveNonExistentSession() {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		dao.setCollection("testSessions");
		dao.clear();
		
		assertFalse("Session shouldn't be deleted", dao.remove(1337));
		Session returnedSession = dao.get(1337);
		
		assertNull("Session should be null", returnedSession);
	}
	
	@Test
	public void testFindByQuery() {
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		dao.setCollection("testSessions");
		dao.clear();
		
		String protocol = "testProtocol";
		
		Session testSession = new Session(0, protocol, "test");
		dao.insert(testSession);
		
		ArrayList<Session> matchedSessions = (ArrayList<Session>) dao.findByProtocol(protocol);
		assertTrue(matchedSessions.size() == 1);
		assertTrue(matchedSessions.get(0).getProtocol().equals(protocol));
	}
	
	@Test
	public void testUpdatingSession() {
		
		MongoDBSessionDAO dao = new MongoDBSessionDAO();
		dao.setCollection("testSessions");
		dao.clear();
		
		Session testSession = new Session(0, "MQTT", "JSON");
		dao.insert(testSession);
		
		long testTime = (long) 11010101;
		
		dao.update(0, "timeStamp", testTime);
		Session resultSession = dao.get(0);
		assertNotNull("Session should not be null", resultSession);
		assertTrue(resultSession.getTimeStamp() == testTime);
		
	}
	
	// find by timestamp

}
