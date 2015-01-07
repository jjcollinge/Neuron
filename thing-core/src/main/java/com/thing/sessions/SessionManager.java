package com.thing.sessions;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thing.api.components.Service;


public class SessionManager {

	private static final Logger log = Logger.getLogger( SessionManager.class.getName() );
	
	private static int counter = 0;
	
	public static synchronized int generateId() {
		return counter++;
	}

}
