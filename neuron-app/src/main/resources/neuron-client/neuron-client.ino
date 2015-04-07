#include <ArduinoJson.h>

#include <SPI.h>
#include <Ethernet.h>
#include <IPStack.h>
#include <Countdown.h>
#include <MQTTClient.h>


#define MS_PROXY "192.168.1.68"
#define MQTT_PORT 1883

byte mac[] = { 0x00, 0xFF, 0xBB, 0xCC, 0xDE, 0x02 };

#define MQTT_CLIENTID "00ffbbccde02"

EthernetClient c;
IPStack ipstack(c);

MQTT::Client<IPStack, Countdown, 140, 1> client = MQTT::Client<IPStack, Countdown, 140, 1>(ipstack);

byte id;
bool registered = false;
//bool tempStream = false;
bool LEDon = false;

////const PROGMEM char SENSOR_REQ_TOPIC_C[] = {"devices/0/sensors/0"};
const PROGMEM char SENSOR_RES_TOPIC_C[] = {"devices/0/sensors/0/stream/response"};

void messageArrived(MQTT::MessageData& md) {
  Serial.println("Message Received");
  
  MQTT::Message &message = md.message;  
  
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
    
  if (strcmp(topic, "1234") == 0) {
    
    Serial.println("Handling registration response");
    StaticJsonBuffer<50> jsonBuffer;

    JsonObject& root = jsonBuffer.parseObject(payload);
    const char* cid = root["payload"];
    id = atoi(cid);
    Serial.print("Set id: ");
    Serial.println(id);
    Serial.println("Registered");
    
  } else if (strcmp(topic, "devices/0/ping/request") == 0) {
    
    Serial.println("Handling PING request");
    char res[] = "{'sessionId':'0'}";
    MQTT::Message response;
    response.qos = MQTT::QOS0;
    response.retained = false;
    response.payload = res;
    response.payloadlen = strlen(res);
    client.publish("devices/0/ping/response", message);
  } else if (strcmp(topic, "devices/0/actuators/0") == 0) {
    
    Serial.println("Handling actuator request");
    if(LEDon) {
      Serial.println("Turning LED off");
      LEDon = false;
    } else {
      Serial.println("Turning LED on");
      LEDon = true;
    }
  } else {
    Serial.println("Unsupported topic");
  }
}

void setup() {
  Serial.begin(9600);
  Ethernet.begin(mac);
  delay(2000);
}

void loop() {
  int rc = -1;
  // Connect
  if (!client.isConnected()) {
    Serial.println("Connecting to Neuron");
    while (rc != 0) {
      rc = ipstack.connect("192.168.1.68", 1883);
    }
    MQTTPacket_connectData data = MQTTPacket_connectData_initializer;
    data.MQTTVersion = 3;
    data.clientID.cstring = MQTT_CLIENTID;
    while ((rc = client.connect(data)) != 0);
    Serial.println("Connected");
  }

  if (!registered) {
    Serial.println("Registering profile...");
    rc = client.subscribe("#", MQTT::QOS0, messageArrived);
    if (rc != 0) {
      Serial.println("Failed to subscribe");
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
      Serial.print("Registration publication failed with rc: ");
      Serial.println(rc);
    } else {
      Serial.println("Registration published");
      registered = true;
    }
  }
  client.yield(1000);
}

