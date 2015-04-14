# Neuron (In development)
-----------------------------------------------
**Neuron** is a IOTs platform designed to expose a collection of devices, sensors and actuators in a standard format to downstream applications. The platform handles device registration, device activity management, bi-directional device communication and exposes them over a RESTful API.

The project is a M2e (Maven to Eclipse) project and is distributed amongst several modular (although interdependent) jar files.

###Features:
* Device management
* Device, sensor and actuator discovery
* Remote access to sensor and actuator functionality and data
* Dynamic device filtration (No stale devices)
* Asynchronous sensor streaming
* Semantic RESTful API
* Actuator control commands
* Configuration driven

###Setup:

#####Pre:
As mentioned above, this is an m2e project. This means that the m2e Eclipse plugin will be require to build the code from source.
* Eclipse IDE (Kepler) - JavaEE edition
* m2e Eclipse plugin
* MongoDB server
* JDK 1.8
* Mosquitto 1.3.5
* Maven

#####Ports:
9998 = Neuron web server
27015 = MongoDB server
1883 = Mosquitto server

#####Instructions:
* Import the source code into a new m2e project
* Build the project/source code
* Run the project or jar files

#####API URLs
localhost:9998/api/devices = all devices
localhost:9998/api/devices/0/sensors = all sensors for device 0
localhost:9998/api/devices/0/actuators = all actuators for device 0
localhost:9998/api/devices/0/sensors/0 = sensor 0 on device 0
localhost:9998/api/devices/0/actuators/0 (GET) = actuator 0 on device 0
localhost:9998/api/devices/0/sensors/0/stream (GET) = start sensor 0 on device 0 streaming
localhost:9998/api/devices/0/sensors/0/stream (POST) = stop sensor 0 on device 0 streaming
localhost:9998/api/devices/0/actuators/0 (POST) = send command to actuator 0 on device 0

#####Devices
An Arduino sketch is located at the path: neuron-app/src/main/resources/arduino-client

A device simulator script is located at the path: neuron-app/src/main/resources/

#####Client
[Please refer to this client repo for an example API client](https://github.com/jjcollinge/Neuron-management)
