package com.neuron.api.components.services;

import com.neuron.api.data.ServiceConfiguration;

public interface Service {

	public abstract void setup(ServiceConfiguration config);

	public abstract void start();

	public abstract void stop();

}
