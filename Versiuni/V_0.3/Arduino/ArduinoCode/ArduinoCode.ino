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

#define turration_on  80
#define turration_off 0

HX711 scale;
SoftwareSerial btm(7,8); //(TX HC-05 , RX HC-05)
int index = 0; 
char data[18]; 
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
int motor_pin(int motor_number){
  switch(motor_number){
    case 0: return motor1; break;
    case 1: return motor2; break;
    case 2: return motor3; break;
    case 3: return motor4; break;
    case 4: return motor5; break;
    case 5: return motor6; break;
    default: return -1;
  }
}
void run_command(int motor, int amount){
  int pin = motor_pin(motor);
  bool pass = false;
  if(pin == -1){
     btm.println("Error: Invalid motor number");
  }
  else{
    scale.tare();
    analogWrite(pin, turration_on);
    while(pass == false){
       if((int)scale.get_units() >= amount){
          pass = true;
          break;
       }
       delay(10);
    }
    analogWrite(pin, turration_off);
    btm.println((int)scale.get_units());
    scale.tare();
  }
}

void waiting_for_message(){
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
}

void decode_message(){
  for(int i=0; i<index; i++){
    if(data[i]>0){
      run_command(i, int(data[i]));
    }
  }
  btm.println("Drink finished!");
}

void loop() {
   waiting_for_message();
   if(flag){
      for(int i=0; i<6; i++){
        Serial.print((int)data[i]);
        Serial.print(" ");
      }
      Serial.print("\n");
      decode_message();
   }
   flag = false;
   index = 0;
   data[0] = '\0'; 
}
