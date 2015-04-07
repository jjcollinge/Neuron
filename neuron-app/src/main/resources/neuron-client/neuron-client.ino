#include <ArduinoJson.h>

#include <SPI.h>
#include <Ethernet.h>
#include <IPStack.h>
#include <Countdown.h>
#include <MQTTClient.h>


#define MS_PROXY "192.168.1.68"
#define MQTT_PORT 1883

byte mac[] = { 0x00, 0xFF, 0xBB, 0xCC, 0xDE, 0x02 };

#define MQTT_CLIENTID "d:quickstart:iotsample-arduino:00ffbbccde02"

EthernetClient c;
IPStack ipstack(c);

MQTT::Client<IPStack, Countdown, 100, 1> client = MQTT::Client<IPStack, Countdown, 100, 1>(ipstack);

int id;
bool registered = false;
//bool tempStream = false;
//bool LEDisOn = false;

char PING_REQ_TOPIC_C[35];
char PING_RES_TOPIC_C[35];
//char CONFIG_TOPIC_C[35];
char SENSOR_TOPIC_C[35];
char LED_TOPIC_C[35];

void messageArrived(MQTT::MessageData& md) {
  Serial.println("Message Received");
  
  MQTT::Message &message = md.message;  
  String payload;
  String topic;
  
  int payloadLen = message.payloadlen;
  int topicLen = md.topicName.lenstring.len;
  
  int x = 0;
  int y = 0;
  
  // topic first
  while(x < topicLen) {
     topic += md.topicName.lenstring.data[x];
     x++;
  }
  // payload next
  while(y < payloadLen) {
     payload += md.topicName.lenstring.data[x + y];
     y++;
  }
  
  Serial.print("Payload: ");
  Serial.println(payload);
  Serial.print("Topic: ");
  Serial.println(topic);
  
  if(topic.length() == 0) return; // drop any outgoing messages which are caught
    
  if (topic.indexOf("1234") >= 0) {
    Serial.println("Handling registration response");
    StaticJsonBuffer<50> jsonBuffer;

    char plc[payload.length()+1];
    payload.toCharArray(plc, payload.length()+1);
    JsonObject& root = jsonBuffer.parseObject(plc);
    const char* cid = root["payload"];
    id = atoi(cid);
    Serial.print("Set id: ");
    Serial.println(id);

    sprintf(PING_REQ_TOPIC_C, "devices/%i/ping/request", id);
    sprintf(PING_RES_TOPIC_C, "devices/%i/ping/response", id);
    //sprintf(CONFIG_TOPIC_C, "devices/%i/configure", id);
    sprintf(SENSOR_TOPIC_C, "devices/%i/sensors/0", id);
    sprintf(LED_TOPIC_C, "devices/%i/actuators/0", id);

    Serial.println("Subscribing to: ");
    Serial.println(PING_REQ_TOPIC_C);
    //Serial.println(CONFIG_TOPIC_C);
    Serial.println(SENSOR_TOPIC_C);
    Serial.println(LED_TOPIC_C);

    // using wildcard so don't need subscriptions
    /*client.subscribe(PING_REQ_TOPIC_C, MQTT::QOS0, messageArrived);
    client.subscribe(CONFIG_TOPIC_C, MQTT::QOS0, messageArrived);
    client.subscribe(SENSOR_TOPIC_C, MQTT::QOS0, messageArrived);
    client.subscribe(LED_TOPIC_C, MQTT::QOS0, messageArrived);*/
    Serial.println("Registered");
    
  } else if (topic.equals(PING_REQ_TOPIC_C)) {
    Serial.println("Handling PING request");
    char res[30];
    sprintf(res, "{'sessionId':'%i'}", id);
    MQTT::Message response;
    response.qos = MQTT::QOS0;
    response.retained = false;
    response.payload = res;
    response.payloadlen = strlen(res);
    client.publish(PING_RES_TOPIC_C, message);
  } else {
    Serial.println("Unsupported topic: " + topic);
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
    char registration[] PROGMEM = "{'regAddress':'1234','device':{'name':'oven'}}";
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

