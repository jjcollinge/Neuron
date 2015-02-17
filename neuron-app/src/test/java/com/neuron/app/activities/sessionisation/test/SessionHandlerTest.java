package com.neuron.app.activities.sessionisation.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neuron.api.core.SessionDaemon;
import com.neuron.api.model.Session;
import com.neuron.app.activities.registration.Registration;
import com.neuron.app.activities.sessionisation.SessionHandler;
import com.neuron.app.activities.sessionisation.mock.SessionDaemonMock;

public class SessionHandlerTest {

	private static SessionHandler handler;
	private static SessionDaemonMock daemon;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		handler = new SessionHandler();
		daemon = new SessionDaemonMock();
		handler.setDaemon(daemon);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		daemon.resetLastSession();
	}

	@Test
	public void testAddingNewSession() {
		
		// Given
		Registration registration = new Registration();
		registration.addProperty("status", "200");
		registration.addProperty("format", "mock");
		registration.addProperty("protocol", "mock");
		registration.setRegistrationAddress("0000");
		registration.addProperty("id", "12");
		
		// When
		handler.onRegistration(registration);
		
		// Should
		Session actual = new SessionDaemonMock().getLastSession();
		assertEquals(12, actual.getId());
		assertEquals("mock", actual.getContext().getProtocol());
		assertEquals("mock", actual.getContext().getFormat());
		assertEquals("0000", actual.getProperty("registrationAddress").get(0));
	}

	@Test
	public void testAddingSessionFromBadRegistration() {
		
		// Given
		Registration registration = new Registration();
		registration.addProperty("status", "400");
		
		// When
		handler.onRegistration(registration);
		
		// Should
		Session actual = new SessionDaemonMock().getLastSession();
		assertNull(actual);
	}
	
}
