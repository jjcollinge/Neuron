#include <ArduinoJson.h>

#include <SPI.h>
#include <Ethernet.h>
#include <IPStack.h>
#include <Countdown.h>
#include <MQTTClient.h>


#define MS_PROXY "10.108.196.24"
#define MQTT_PORT 1883

byte mac[] = { 0x00, 0xFF, 0xBB, 0xCC, 0xDE, 0x02 };

#define MQTT_CLIENTID "00ffbbccde02"

EthernetClient c;
IPStack ipstack(c);

MQTT::Client<IPStack, Countdown, 140, 1> client = MQTT::Client<IPStack, Countdown, 140, 1>(ipstack);

/**
*  Many of the values in this application are hardcoded to save on memory usage.
*  This is only for demonstration purposes. Some of the older commits contain
*  more dynamic code to handle registraton but it required too much SRAM.
**/

bool registered = false;
bool tempStream = false;
bool LEDon = false;
const int sensorPin = A0;

const PROGMEM char SENSOR_RES_TOPIC_C[] = {"devices/0/sensors/0/stream/response"};

void messageArrived(MQTT::MessageData& md) {
  Serial.println(F("Message Received"));
  
  MQTT::Message &message = md.message;  
  
  /** Parse payload and topic **/
  
  byte payloadLen = message.payloadlen;
  byte topicLen = md.topicName.lenstring.len;
  char payload[payloadLen+1];
  char topic[topicLen+1];
  
  byte x = 0;
  byte y = 0;
  
  // topic first
  while(x < topicLen) {
     topic[x] = md.topicName.lenstring.data[x];
     x++;
  }
  topic[topicLen] = '\0';
  
  // payload next
  while(y < payloadLen) {
     payload[y] = md.topicName.lenstring.data[x + y];
     y++;
  }
  payload[payloadLen] = '\0';
  if(topicLen == 0) return; // drop any outgoing messages which are caught
  
  Serial.print("Payload: ");
  Serial.println(payload);
  Serial.print("Topic: ");
  Serial.println(topic);
    
  /** Handle topics **/
  if (strcmp(topic, "1234") == 0) {
    
    Serial.println(F("Handling registration response"));
    /* NOT NEEDED AS ID IS HARDCODED FOR DEMO
    StaticJsonBuffer<50> jsonBuffer;
    JsonObject& root = jsonBuffer.parseObject(payload);
    const char* cid = root["payload"];
    id = atoi(cid);
    Serial.print("Set id: ");
    Serial.println(id);*/
    Serial.println(F("Registered"));
    
  } else if (strcmp(topic, "devices/0/ping/request") == 0) {
    
    Serial.println(F("Handling PING request"));
    char res[] = "{'sessionId':'0'}";
    MQTT::Message response;
    response.qos = MQTT::QOS0;
    response.retained = false;
    response.payload = res;
    response.payloadlen = strlen(res);
    client.publish("devices/0/ping/response", message);
  } else if (strcmp(topic, "devices/0/actuators/0") == 0) {
    
    Serial.println(F("Handling LED command"));
    if(LEDon) {
      Serial.println(F("Turning LED off"));
      LEDon = false;
      digitalWrite(5, LOW);
    } else {
      Serial.println(F("Turning LED on"));
      LEDon = true;
      digitalWrite(5, HIGH);
    }
  } else if (strcmp(topic, "devices/0/sensors/0") == 0) {
    
    Serial.println(F("Handling sensor request"));
    StaticJsonBuffer<30> jsonBuffer;
    const JsonObject& root = jsonBuffer.parseObject(payload);
    const char* cmd = root["payload"];
    if(strcmp(cmd, "START_STREAM") == 0) {
      tempStream = true;
    } else if(strcmp(cmd, "STOP_STREAM") == 0) {
      tempStream = false;
    }
  } else {
    
    Serial.println(F("Unsupported topic"));
  }
}

void setup() {
  Serial.begin(9600);
  Ethernet.begin(mac);
  pinMode(5, OUTPUT);
  delay(2000);
}

void loop() {
  int rc = -1;
  
  // Connect
  if (!client.isConnected()) {
    Serial.println(F("Connecting to Neuron"));
    while (rc != 0) {
      rc = ipstack.connect("192.168.1.68", 1883);
    }
    MQTTPacket_connectData data = MQTTPacket_connectData_initializer;
    data.MQTTVersion = 3;
    data.clientID.cstring = MQTT_CLIENTID;
    while ((rc = client.connect(data)) != 0);
    Serial.println(F("Connected"));
  }

  // Register
  if (!registered) {
    Serial.println(F("Registering profile..."));
    rc = client.subscribe("#", MQTT::QOS0, messageArrived);
    if (rc != 0) {
      Serial.println(F("Failed to subscribe"));
      return;
    }
    char* registration = "{'regAddress':'1234','device':{'sensors':[{'sense':'tmp'}],'actuators':[{'desc':'LED','options':['ON','OFF']}]}}";
    MQTT::Message message;
    message.qos = MQTT::QOS0;
    message.retained = false;
    message.payload = registration;
    message.payloadlen = strlen(registration);
    rc = client.publish("register", message);
    if (rc != 0) {
      Serial.print(F("Registration publication failed with rc: "));
      Serial.println(rc);
    } else {
      Serial.println(F("Registration published"));
      registered = true;
    }
  }
  
  // Stream
  if(tempStream) { 
    const int temperature = (((analogRead(sensorPin)/1024.0) * 5) -.5) * 100;
    char msg[40];
    sprintf(msg, "{\"sessionId\":\"0\",\"data\":%i}", temperature);
    MQTT::Message message;
    message.qos = MQTT::QOS0;
    message.retained = false;
    message.payload = msg;
    message.payloadlen = strlen(msg);
    client.publish("devices/0/sensors/0/stream/response", message);
    delay(2000);
  }
  
  client.yield(1000);
}

