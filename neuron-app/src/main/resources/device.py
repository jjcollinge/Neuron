#!/usr/bin/python

import sys
import paho.mqtt.client as mqtt
import time
import json
from random import randint

#--------------------------------------------
# VARIABLES
#--------------------------------------------

HOST = "localhost"
PORT = 1883
REG_REQUEST = "register"
REG_RESPONSE = str(randint(1000, 9999))

#-------------------------------------------
# MODEL
#-------------------------------------------
class Device:
	uuid = -1
        refresh_rate = 10
        
	def __init__(self, name):
		self.name = name
		self.sensors = []
		self.actuators = []

	def addSensor(self, sensor):
		self.sensors.append(sensor)

	def addActuator(self, actuator):
		self.actuators.append(actuator)

class Sensor:
	streaming = False;
	value = 0.0

	def __init__(self, uuid, desc, sense, unit, tags):
		self.uuid = uuid
		self.desc = desc
		self.sense = sense
		self.unit = unit
		self.tags = tags

class Actuator:

	def __init__(self, uuid, desc, tags):
		self.uuid = uuid
		self.desc = desc
		self.options = []
		self.tags = tags

	def addOption(self, option):
		self.options.append(option)

#-------------------------------------------
# CALLBACKS
#-------------------------------------------
def on_connect(client, userdata, rc):
	print("Connected with result code "+str(rc))

def on_message(client, userdata, msg):
	print("Received "+str(msg.payload)+" on topic "+msg.topic)

	# Response to registration request
	if(msg.topic == REG_RESPONSE):
		jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		device.uuid = str(pyObj["payload"])
		client.subscribe("devices/"+device.uuid+"/ping/request")
		client.subscribe("devices/"+device.uuid+"/configure")
		for sensor in device.sensors:
			client.subscribe("devices/"+device.uuid+"/sensors/"+sensor.uuid)
		for actuator in device.actuators:
			client.subscribe("devices/"+device.uuid+"/actuators/"+actuator.uuid)
		print("Completed registration with id: " + device.uuid)

        # Configure device
        elif(msg.topic == "devices/"+device.uuid+"/configure"):
                print("Configuring device")
                jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		device.refresh_rate = str(pyObj["payload"])

	# Request for ping response to check activity
	elif(msg.topic == "devices/"+device.uuid+"/ping/request"):
		print("Responding to PING")
		client.publish("devices/"+device.uuid+"/ping/response", "{\"sessionId\":"+device.uuid+"}")

	# Request for sensor operation
	elif("sensor" in msg.topic):
		for sensor in device.sensors:
			if(msg.topic == "devices/"+device.uuid+"/sensors/"+sensor.uuid):
				print("Handling request for sensor "+sensor.uuid)
				jsonData = str(msg.payload)
				pyObj = json.loads(jsonData)
				request = pyObj["payload"]
				if(request == "GET"):
					print("GET request")
					client.publish("devices/"+device.uuid+"/sensors/"+sensor.uuid+"/response", sensor.value)
					value = value + 0.2
				elif(request == "START_STREAM"):
					print("Staring device stream");
					sensor.streaming = True;
				elif(request == "STOP_STREAM"):
					print("Stopping device stream");
					sensor.streaming = False;

	# Request for actuator operation
	elif("actuator"  in msg.topic):
		for actuator in device.actuators:
			if(msg.topic == "devices/"+device.uuid+"/actuators/"+actuator.uuid):
				print("Handling request for actuator "+actuator.uuid)
				jsonData = str(msg.payload)
				pyObj = json.loads(jsonData)
				request = pyObj["payload"]
				for opt in actuator.options:
					if(request == opt):
						print "doing " + opt

def on_publish(client, userdata, mid):
	print("Published")

def on_log(client, obj, level, string):
	print string

def on_subscribe(client, obj, mid, granted_qos):
	print("Subscribed: "+str(mid)+" "+str(granted_qos))


#-------------------------------------------
# SCRIPT
#-------------------------------------------

client = mqtt.Client()

# Set paho callbacks
client.on_connect 	= on_connect
client.on_message 	= on_message
client.on_publish 	= on_publish
client.on_log 		= on_log
client.on_subscribe = on_subscribe

fileNumber = 0;

if len(sys.argv) > 1:
	fileNumber = sys.argv[1]

# Read in json document and initialise device/sensors
file = open("device" + str(fileNumber) + ".json", 'r');
descriptor = file.read();
jsonDevice = json.loads(descriptor);
device = Device(jsonDevice['name'])
c = 0
for jsonSensor in jsonDevice['sensors']:
	sensor = Sensor(str(c), jsonSensor['desc'], jsonSensor['sense'], jsonSensor['unit'], jsonSensor['tags'])
	device.addSensor(sensor)
	c = c + 1
c = 0
for jsonActuator in jsonDevice['actuators']:
	actuator = Actuator(str(c), jsonActuator['desc'], jsonActuator['tags'])
	for jsonOpt in jsonActuator['options']:
		actuator.addOption(jsonOpt)
	device.addActuator(actuator)
	c = c + 1

# Connect to broker
client.connect(HOST, PORT, 60)
# Subscribe to registration response topic
client.subscribe(REG_RESPONSE)
# Build registration json string
registration = "{\"regAddress\":\""+ REG_RESPONSE +"\",\"device\":"+ descriptor +"}"
print("Registration: " + registration)
# Publish registration json string to registration topic
client.publish(REG_REQUEST, registration)
# Service any network traffic in a separate thread
client.loop_start()
# Loop forever
while True:
	for sensor in device.sensors:
		if(sensor.streaming):
			sensor.value = sensor.value + 0.1
			client.publish("devices/"+str(device.uuid)+"/sensors/"+str(sensor.uuid)+"/stream/response", "{ \"sessionId\":"+str(device.uuid)+", \"data\": "+str(sensor.value)+"}")
	
	# Delay cycle
	time.sleep(int(device.refresh_rate))
