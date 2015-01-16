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

	def __init__(self, descriptor):
		self.descriptor = descriptor
		self.sensors = []

	def addSensor(self, sensor):
		self.sensors.append(sensor)

class Sensor:
	streaming = False;
	value = 0.0

	def __init__(self, uuid, sense, unit, type):
		self.uuid = uuid
		self.sense = sense
		self.unit = unit
		self.type = type


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
		for sensor in device.sensors:
			client.subscribe("devices/"+device.uuid+"/sensors/"+sensor.uuid)

	# Request for ping response to check activity
	elif(msg.topic == "devices/"+device.uuid+"/ping/request"):
		print("Responding to PING")
		client.publish("devices/"+device.uuid+"/ping/response", "{\"id\":"+device.uuid+"}")

	# Request for sensor operation
	else:
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
					sensor.streaming = True;
				elif(request == "STOP_STREAM"):
					sensor.streaming = False;

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
sensor = Sensor("0", "temperature", "celcius", "float")
device.addSensor(sensor)

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
			client.publish("devices/"+device.uuid+"/sensors/"+sensor.uuid+"/stream/response", sensor.value)
	
	# Delay cycle
	time.sleep(10)