package com.neuron.rest;


public class DeviceProxyFactory {
	
	public DeviceProxy getDeviceProxy(String type) {
		
		DeviceProxy proxy = null;
		if(type.equalsIgnoreCase("MQTT")) {
			proxy = new MqttDeviceProxy();
		}
		return proxy;
	}

}
