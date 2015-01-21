package com.thing.api.components;

public class SessionIdGenerator {

	private static int counter = 0;

	public static int generateId() {
		return counter++;
	}

}
