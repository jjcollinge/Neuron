int LEDPIN = 3;
int TEMPPIN = 0;
int ID = -1;
String sID = "";


String PING_TOPIC = "";
String JOB_TOPIC= "";
String REG_RESPONSE = "9999";

#include <ArduinoJson.h>

void setup() {
  Serial.begin(9600);

  //Set up components
  pinMode(LEDPIN, OUTPUT);
  digitalWrite(LEDPIN, LOW); 

  //Register
  sendMessage(String("register"), String("{\"returnAddress\":\"9999\",\"device\":{\"manufacturer\":\"Arduino\",\"model\":\"Uno\",\"gps\":[1,2,3],\"sensors\": [{\"pin\":3, \"sense\":\"temperature\",\"unit\":\"celcius\",\"type\":\"float\"}],\"actuators\":[]}}"));
}

void sendMessage(String topic, String msg) {
  
  char topicBuf[50];
  char msgBuf[256];
  topic.toCharArray(topicBuf, 50);
  msg.toCharArray(msgBuf, 256);
  
  StaticJsonBuffer<400> jsonBuffer;
  
  JsonObject& message = jsonBuffer.createObject();
  message["topic"] = topicBuf;
  message["data"] = msgBuf;
  
  message.printTo(Serial);
  Serial.println();
  
}

void getMessage(String& topic, String& msg) {
 
  msg = Serial.readString();
  
  Serial.println("Read in data: " + msg);
  
  StaticJsonBuffer<256> jsonBuffer;
  
  char json[256];
  msg.toCharArray(json, 256);
  
  JsonObject& message = jsonBuffer.parseObject(json);
  
  if(!message.success()) {
    Serial.println("Parsing message failed");
    return;
  }
  
  topic = message["topic"];
  msg = message["data"];
  Serial.println("Topic: " + topic + " Msg: " + msg);
  
}

void handleRegistrationResponse(String res) {
  Serial.println("Handling registration");
    
  ID = res.toInt();
  sID = res;
  
  PING_TOPIC = "device/"+sID+"/ping/request";
  JOB_TOPIC = "device/"+sID+"/job/request";
  
  Serial.println("Set my PING topic to: " + PING_TOPIC);
  Serial.println("Set my JOB topic to: " + JOB_TOPIC);
}

void handlePingRequest(String req) {
  Serial.println("Handling ping request");
  
  String res = "PONG";
  String idStr = String(ID);
  
  String resTopic = "device/"+idStr+"/ping/response";
  Serial.println("Responding on " + resTopic);
  sendMessage(resTopic, res);
}

double convertVoltsToTemp(int RawADC) {  //Function to perform the fancy math of the Steinhart-Hart equation
 double Temp;
 Temp = log(((10240000/RawADC) - 10000));
 Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
 Temp = Temp - 273.15;              // Convert Kelvin to Celsius
 Temp = (Temp * 9.0)/ 5.0 + 32.0; // Celsius to Fahrenheit - comment out this line if you need Celsius
 return Temp;
}
void loop() {
 
  delay(1000);
  
 //Handle request
 if(Serial.available()) {
  
   Serial.println("Data available");
   
   String topic;
   String msg;
   getMessage(topic, msg);
   
   String pin = "0";
   
   if(topic == "9999") {
     handleRegistrationResponse(msg);
   } else if(topic == PING_TOPIC) {
     handlePingRequest(msg);
   } else if(topic == "device/"+sID+"/sensor/"+pin) {
      // Get sensor value
      if(msg == "GET") {
         int volt = analogRead(TEMPPIN);
         String v = String(v);
         Serial.println("Volts: " + v);
         float floatTemp = convertVoltsToTemp(volt);
         static char out[15];
         dtostrf(floatTemp, 6, 2, out);
         String s = String(out);
         sendMessage("device/"+sID+"/sensor/"+pin+"/response", s); 
      } else if(msg == "GET_BIND") {
         while(true) {
           int volt = analogRead(TEMPPIN);
           String v = String(v);
           Serial.println("Volts: " + v);
           float floatTemp = convertVoltsToTemp(volt);
           static char out[15];
           dtostrf(floatTemp, 6, 2, out);
           String s = String(out);
           sendMessage("device/"+sID+"/sensor/"+pin+"/response", s); 
           delay(1000);
         } 
      }
   }
   
 }
  
}
