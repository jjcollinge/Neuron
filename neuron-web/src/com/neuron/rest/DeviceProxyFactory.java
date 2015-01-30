package com.neuron.rest;

import com.neuron.api.data.Context;


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
