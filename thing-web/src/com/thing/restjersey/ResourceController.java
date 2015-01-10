package com.thing.restjersey;

import com.thing.api.events.DeviceEvent;
import com.thing.api.events.DeviceEventListener;

/**
 * Name: ResourceController
 * --------------------------------
 * Desc: Responsible for dynamically generating resources
 * 		 from classes
 * 
 * @author jcollinge
 *
 */
public class ResourceController implements DeviceEventListener {

	@Override
	public void onDevicesChanged(DeviceEvent event) {
		
	}
	
}
