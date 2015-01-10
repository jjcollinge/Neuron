#!/usr/bin/python

import paho.mqtt.client as mqtt
import time
import json
from random import randint

HOST = "localhost"
PORT = 1883
REG_REQUEST = "register"
REG_RESPONSE = str(randint(1000, 9999))

devId = -1
value = 0.1

def on_connect(client, userdata, rc):
	print("Connected with result code "+str(rc))

def on_message(client, userdata, msg):
	global devId, value
	print("Received "+str(msg.payload)+" on topic "+msg.topic)
	if(msg.topic == REG_RESPONSE):
		jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		devId = pyObj["data"]
		client.subscribe("devices/"+str(devId)+"/ping/request")
		client.subscribe("devices/"+str(devId)+"/sensors/0")
	if(msg.topic == "devices/"+str(devId)+"/ping/request"):
		client.publish("devices/"+str(devId)+"/ping/response", "{\"id\":"+str(devId)+"}")
	if(msg.topic == "devices/"+str(devId)+"/sensors/0"):
		print("Handling job")
		jsonData = str(msg.payload)
		pyObj = json.loads(jsonData)
		request = pyObj["data"]
		if(request == "GET"):
			print("Getting sensor 0")
			client.publish("devices/"+str(devId)+"/sensors/0/response", value);
			value = value + 0.2

def on_publish(client, userdata, mid):
	print("Published")

def on_log(client, obj, level, string):
	print string

def on_subscribe(client, obj, mid, granted_qos):
	print("Subscribed: "+str(mid)+" "+str(granted_qos))

client = mqtt.Client()

client.on_connect = on_connect
client.on_message = on_message
client.on_publish = on_publish
client.on_log = on_log
client.on_subscribe = on_subscribe

# Connect to broker
client.connect(HOST, PORT, 60)
# Subscribe to registration response topic
client.subscribe(REG_RESPONSE)
# Build registration json string
registration = "{\"returnAddress\":\""+ REG_RESPONSE +"\",\"device\":{ \"id\":0,\"manufacturer\":\"Raspberry Pi\",\"model\":\"B+\",\"gps\":[123.4,567.8,910.1],\"sensors\":[{\"sense\":\"temperature\",\"unit\":\"celcius\",\"type\":\"float\", \"value\":\"0.0\"}],\"actuators\":[]}}"
print("Registration: " + registration)
# Publish registration json string to registration topic
client.publish(REG_REQUEST, registration)
# Loop forever
client.loop_forever()
