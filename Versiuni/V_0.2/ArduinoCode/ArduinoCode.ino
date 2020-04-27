#include <SoftwareSerial.h>
#include "HX711.h"
#include <string.h>

#define calibration_factor 464.0
#define DOUT  A1
#define CLK  A0
#define motor1 3
#define motor2 5
#define motor3 6
#define motor4 9
#define motor5 10
#define motor6 11

HX711 scale;
SoftwareSerial btm(7,8); //(TX HC-05 , RX HC-05)
int index = 0; 
char data[10]; 
char c; 
bool flag = false;
bool print_weight = false;

void setup_scale(){
  scale.begin(DOUT, CLK);
  scale.set_scale(calibration_factor); //This value is obtained by using the SparkFun_HX711_Calibration sketch
  scale.tare();
}
//void setup_timer_1(){
//  TCCR1A = 0;            // undo the Arduino's timer configuration
//  TCCR1B = 0;            // ditto
//  TCNT1  = 0;            // reset timer
//  OCR1A  = 62500/2;    // period = 62500 clock tics
//  TCCR1B = _BV(WGM12)    // CTC mode, TOP = OCR1A
//         | _BV(CS12);    // clock at F_CPU/256
//  TIMSK1 = _BV(OCIE1A);  // interrupt on output compare A
//}
void setup() {
  btm.begin(9600);
  Serial.begin(9600);
  setup_scale();
//  setup_timer_1();

  pinMode(motor1, OUTPUT);
  pinMode(motor2, OUTPUT);
  pinMode(motor3, OUTPUT);
  pinMode(motor4, OUTPUT);
  pinMode(motor5, OUTPUT);
  pinMode(motor6, OUTPUT);
  analogWrite(motor1, 0);
  analogWrite(motor2, 0);
  analogWrite(motor3, 0);
  analogWrite(motor4, 0);
  analogWrite(motor5, 0);
  analogWrite(motor6, 0);
  
}
//ISR(TIMER1_COMPA_vect)
//{
//  if(print_weight){
//    Serial.println((int)scale.get_units());
//    btm.print((int)scale.get_units());
//    btm.println(" g");
//  }
//}
void loop() {
  if(btm.available() > 0){
     while(btm.available() > 0){ 
          c = btm.read();
          delay(10); //Delay required 
          data[index] = c; 
          index++; 
     }
     data[index] = '\0'; 
     flag = true;
   }
   if(flag){
      if(strcmp(data,"Start")==0){
         Serial.println("Start!");
         btm.println("Start!");
         print_weight = true;
      }
      else
        if(strcmp(data,"Stop")==0){
          Serial.println("Stop!");
          btm.println("Stop!");
          print_weight = false;
        }
        else{
          if(data[0] == '1'){
            switch(data[1]){
              case '1':
                if(data[2] == '0'){
                  btm.println("Stop motor1");
                  analogWrite(motor1, 0);
                }
                else{
                  btm.println("Start motor1");
                  analogWrite(motor1, 120);
                }
                break;
              case '2':
                if(data[2] == '0'){
                  btm.println("Stop motor2");
                  analogWrite(motor2, 0);
                }
                else{
                  btm.println("Start motor2");
                  analogWrite(motor2, 120);
                }
                break;
              case '3':
                if(data[2] == '0'){
                  btm.println("Stop motor3");
                  analogWrite(motor3, 0);
                }
                else{
                  btm.println("Start motor3");
                  analogWrite(motor3, 120);
                }
                break;
              case '4':
                if(data[2] == '0'){
                  btm.println("Stop motor4");
                  analogWrite(motor4, 0);
                }
                else{
                  btm.println("Start motor4");
                  analogWrite(motor4, 120);
                }
                break;
              case '5':
                if(data[2] == '0'){
                  btm.println("Stop motor5");
                  analogWrite(motor5, 0);
                }
                else{
                  btm.println("Start motor5");
                  analogWrite(motor5, 120);
                }
                break;
              case '6':
                if(data[2] == '0'){
                  btm.println("Stop motor6");
                  analogWrite(motor6, 0);
                }
                else{
                  btm.println("Start motor6");
                  analogWrite(motor6, 120);
                }
                break;
              default:  Serial.println("Received another message");
                        btm.println("Received another message");
                        break;
            }
          }
          else{
            Serial.println("Received another message");
            btm.println("Received another message");
          }
        }
        
     flag = false;
     index = 0;
     data[0] = '\0'; 
   }
}
