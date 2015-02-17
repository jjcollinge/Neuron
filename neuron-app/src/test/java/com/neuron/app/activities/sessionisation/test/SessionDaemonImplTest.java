package com.neuron.app.activities.sessionisation.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.model.Context;
import com.neuron.api.model.Session;
import com.neuron.app.activities.sessionisation.SessionDaemonImpl;

public class SessionDaemonImplTest {

	private static SessionDaemonImpl daemon;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		daemon = new SessionDaemonImpl();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddANewSessionAndRetrieve() {
		
		// Given
		Session session = new Session(0);
		session.setContext(new Context("test", "test"));
		session.addProperty("test", "test");
		daemon.addSession(session);
		
		// When
		Session actual = daemon.getSession(0);
		
		// Should
		assertEquals(session.getId(), actual.getId());
		assertEquals(session.getContext().getFormat(), actual.getContext().getFormat());
		assertEquals(session.getContext().getProtocol(), actual.getContext().getProtocol());
		
	}
	
	@Test
	public void testAddANewSessionAndUpdate() {
		
		// Given
		Session session = new Session(0);
		session.setContext(new Context("test", "test"));
		session.addProperty("test", "test");
		long condition = session.getTimestamp();
		daemon.addSession(session);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		daemon.updateTimestamp(0);
		
		// When
		Session actual = daemon.getSession(0);
		
		// Should
		assertTrue(actual.getTimestamp() > condition);
		
	}

}
