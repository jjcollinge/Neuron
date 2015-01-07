package com.thing.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterList {

	private ArrayList<Object> objects;
	private ArrayList<Class> classes;
	
	public ParameterList() {
		objects = new ArrayList<Object>();
		classes = new ArrayList<Class>();
	}
	public void addParameters(Object o, Class c) {
		objects.add(o);
		classes.add(c);
	}
	public int getSize() {
		return objects.size();
	}
	@SuppressWarnings("unchecked")
	public <T> T getParameter(int index) throws IndexOutOfBoundsException, ClassCastException {
		return (T) classes.get(index).cast(objects.get(index));
	}
}
