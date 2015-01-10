package com.thing.restjersey;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.thing.api.model.Device;
import com.thing.storage.DataHandler;

@Path("devices/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Device> getDevices() {
		System.out.println("Request for devices");
		ArrayList<Device> devices = DataHandler.getInstance().getDevices();
		return devices;
	}

}
