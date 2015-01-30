package com.neuron.rest;

import com.neuron.api.data.Context;

/**
 * Masks the implementation details from the client and
 * returns a DeviceProxy of a particular implementation
 * based on the incoming context.
 * @author JC
 *
 */
public class DeviceProxyFactory {
	
	public DeviceProxy getDeviceProxy(Context context) {
		
		DeviceProxy proxy = null;	
		String protocol = context.getProtocol();
		
		if(protocol.equalsIgnoreCase("mqtt")) {
			proxy = new MqttDeviceProxy();
		}
		return proxy;
	}

}
