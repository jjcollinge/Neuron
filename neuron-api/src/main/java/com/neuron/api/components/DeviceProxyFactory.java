package com.neuron.api.components;

import java.util.HashMap;

import com.neuron.api.data.Context;

/**
 * Masks the implementation details from the client and
 * returns a DeviceProxy of a particular implementation
 * based on the incoming context.
 * @author JC
 *
 */
public class DeviceProxyFactory {
	
	private static HashMap<String, Class> proxies;
	
	public DeviceProxyFactory() {
		if (proxies == null) {
			proxies = new HashMap<String, Class>();
		}
	}
	
	/**
	 * Register a new proxy type with factory
	 */
	public void registerProxy(String protocol, Class klass) {
		proxies.put(protocol, klass);
	}
	
	public DeviceProxy getDeviceProxy(Context context) {
		
		DeviceProxy proxy = null;	
		String protocol = context.getProtocol();
		Class proxyClass = proxies.get(protocol);
		
		if(proxyClass != null) {
			try {
				proxy = (DeviceProxy) proxyClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return proxy;
	}

}
