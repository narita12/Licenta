#include <string.h>
String msg="";

void setup() {
  Serial.begin(9600);
  Serial.println("Connected!");
}

void loop() {
 readSerial();
 decodeMessage();
}

void readSerial()
{
  msg="";
  char c;
  do{
    while(!Serial.available());
    c=Serial.read();
    msg+=c;
  }while(c!='\n');
}

void decodeMessage(){
  char* delim = "_";
  int type, motor, turration;
  char* command = strtok(msg.c_str(),delim);
  type = atoi(command);
  if(type == 1){
    Serial.println(">> Selected Motor control");
    command = strtok(NULL,delim);
    motor = atoi(command);
    command = strtok(NULL,delim);
    turration = atoi(command);
    _print("Selected motor: ",motor);
    _print("Selected turration: ",turration);
  }
  else
    if(type == 2){
      Serial.println(">> Selected Weight receive");
      _print("Current Weight: ",getWeight());
    }
    else
      Serial.println(">> Erronate type");
}

int getWeight(){
  return 0;
}

void _print(String comment, int value){
  Serial.print(comment);
  Serial.println(value);
}
