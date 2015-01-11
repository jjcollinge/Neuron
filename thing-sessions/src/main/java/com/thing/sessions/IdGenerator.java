package com.thing.sessions;

public class IdGenerator {

	private static int counter = 0;

	public static int generateId() {
		return counter++;
	}

}
