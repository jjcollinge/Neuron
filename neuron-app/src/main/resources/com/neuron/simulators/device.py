#!/usr/bin/python

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
        
	def __init__(self, descriptor):
		self.descriptor = descriptor
		self.sensors = []
		self.actuators = []

	def addSensor(self, sensor):
		self.sensors.append(sensor)

	def addActuator(self, actuator):
		self.actuators.append(actuator)

class Sensor:
	streaming = False;
	value = 0.0

	def __init__(self, uuid, name, sense, unit, type):
		self.uuid = uuid
		self.name = name
		self.sense = sense
		self.unit = unit
		self.type = type

class Actuator:

	def __init__(self, uuid, name):
		self.uuid = uuid
		self.name = name
		self.options = []

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
		print("Completing registration")
		jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		device.uuid = str(pyObj["data"])
		client.subscribe("devices/"+device.uuid+"/ping/request")
		client.subscribe("devices/"+device.uuid+"/configure")
		for sensor in device.sensors:
			client.subscribe("devices/"+device.uuid+"/sensors/"+sensor.uuid)
		for actuator in device.actuators:
			client.subscribe("devices/"+device.uuid+"/actuators/"+actuator.uuid)

        # Configure device
        elif(msg.topic == "devices/"+device.uuid+"/configure"):
                print("Configuring device")
                jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		device.refresh_rate = str(pyObj["data"])

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
				request = pyObj["data"]
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
				request = pyObj["data"]
				if(request == "ON"):
					print("Turning LED on")
				elif(request == "OFF"):
					print("Turning LED off")

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

# Read in json document and initialise device/sensors
file = open("device.json", 'r');
descriptor = file.read();
device = Device(descriptor)
sensor = Sensor("0", "Temperature Sensor", "temperature", "celcius", "float")
device.addSensor(sensor)
actuator = Actuator("0", "LED light bulb")
actuator.addOption("ON")
actuator.addOption("OFF")
device.addActuator(actuator)

# Connect to broker
client.connect(HOST, PORT, 60)
# Subscribe to registration response topic
client.subscribe(REG_RESPONSE)
# Build registration json string
registration = "{\"returnAddress\":\""+ REG_RESPONSE +"\",\"device\":"+ device.descriptor +"}"
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
