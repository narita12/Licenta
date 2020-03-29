#include "HX711.h"
#include <string.h>

#define calibration_factor -7050.0
#define DOUT  A0
#define CLK  A1
#define motor1 3
HX711 scale;

String msg="";

void setup() {
  Serial.begin(9600);
  Serial.println("Connected!");
  scale.begin(DOUT, CLK);
  scale.set_scale(calibration_factor); //This value is obtained by using the SparkFun_HX711_Calibration sketch
  scale.tare();
  pinMode(motor1, OUTPUT);
  analogWrite(motor1, 0);
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
    if(turration < 0 || turration > 255 || motor < 0 || motor > 6)
      Serial.println("Invalid motor number or turration");
    else{
      motor_control(motor,turration);
    }
  }
  else
    if(type == 2){
      Serial.println(">> Selected Weight receive");
      _print("Weigth: ",scale.get_units()," lbs");
    }
    else
      Serial.println(">> Erronate type");
}
void motor_control(int motor, int turration){
  int pin;
  if(motor == 1)
    pin=3;
  analogWrite(pin, turration);
}
void _print(String comment, int value){
  Serial.print(comment);
  Serial.println(value);
}
void _print(String comment, float value, String units){
  Serial.print(comment);
  Serial.print(value);
  Serial.print(" ");
  Serial.println(units);
}
