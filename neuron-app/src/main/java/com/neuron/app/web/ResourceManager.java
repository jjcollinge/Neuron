package com.neuron.app.web;

import java.util.HashMap;

/**
 * In order to avoid creating multiple resources per
 * request and thus invoking creating multiple
 * streams to and from the devices, the resources must
 * be managed. The ResourceManager is responsible for
 * storing resources with their URI as a key. This
 * allows clients to grab any existing resource based
 * only on their URI.
 * @author JC
 *
 */
public class ResourceManager {

	private HashMap<String, Object> resources;
	private static ResourceManager instance;
	
	private ResourceManager() {
		resources = new HashMap<String, Object>();
	}
	
	public static ResourceManager getInstance() {
		if(instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}
	
	/**
	 * Store a new resource keyed by its uri
	 * @param uri The desired URI
	 * @param resource The resource to store
	 */
	public void addResource(String uri, Object resource) {
		resources.put(uri, resource);
	}
	
	/**
	 * Remove a resource
	 * @param resource resource The resource to remove
	 */
	public void removeResource(Object resource) {
		resources.remove(resource);
	}
	
	/**
	 * Return a resource keyed by its uri
	 * @param uri The resource identifier to get
	 * @return Object The resource
	 */
	public Object getResource(String uri) {
		return resources.get(uri);
	}
	
}
