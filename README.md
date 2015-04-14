# Neuron
---------------------
**Neuron** is an IoT platform designed to expose a collection of devices, sensors and actuators in a standard format to downstream applications. The platform handles interfacing with the underlying smart objects and provides a single RESTful API describing the network.

###Features
* Device management
* Device, Sensor and Actuator discovery
* Remote access to Sensor and Actuator functions and data
* Dynamic session management (supporting timeouts)
* Asynchrnous Sensor value streaming
* Semantic API
* Actuator control commands
* Configuration driven design


###Setup

#####Prerequisite
The project is an m2e (Maven 2 Eclipse) project.
* Eclipse IDE (Kepler) - JavaEE version
* m2e Eclipse plugin
* MongoDB server
* JDK 1.8
* Mosquitto 1.3.5
* Maven

#####Ports
9998: Neuron web server
27015: MongoDB server
1883: Mosquitto server

#####Instructions
* Import the source code into a new m2e project
* Build the project / compile the source
* Run the project / run the jar files

#####API Urls

######GET Method
* __/api/devices:__ -> all devices
*  __/api/devices/0:__ -> device 0
*  __/api/devices/0/sensors:__ -> all sensors on device 0
*  __/api/devices/0/actuators:__ -> all actuators on device 0
*  __/api/devices/0/sensors/0:__ -> sensor 0 on device 0
*  __/api/devices/0/actuators/0:__ -> actuator 0 on device 0
*  __/api/devices/0/sensors/0/stream:__ -> START sensor 0 on device 0 streaming its data

######POST Method
*  __/api/devices/0/sensors/0/stream:__ -> STOP sensor 0 on device 0 streaming its data

#####Devices
A sample Arduino sketch is located at: neuron-app/src/main/resources/arduino-client
A device simultator script is located at: neuron-app/src/main/resources/
n.b. the script should be executed using the following command:
>   python device.py [0,1,2]

#####Clients
[ A sample client is hosted at this GitHub repository](https://github.com/jjcollinge/Neuron-management)



