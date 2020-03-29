#include <SoftwareSerial.h>
SoftwareSerial mySerial(8,9);//(TX,RX)
/////////////////////
String msg = "";
void setup() {
  /////////////
  Serial.begin(9600);
  mySerial.begin(9600);
}

void loop() {
  play();
}
