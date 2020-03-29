//void kananMaju(unsigned char kec)
//{
//  digitalWrite(DIR1KA,LOW); //DIR1KA output LOW
//  digitalWrite(DIR2KA,HIGH);//DIR2KA output HIGH
//  analogWrite(PWMKA,kec);   //PWMKA output kec
//}
//
//void kiriMaju(unsigned char kec)
//{
//  digitalWrite(DIR1KI,LOW);
//  digitalWrite(DIR2KI,HIGH);
//  analogWrite(PWMKI,kec);
//}
//
//void kananMundur(unsigned char kec)
//{
//  digitalWrite(DIR1KA,HIGH);//DIR1KA output HIGH
//  digitalWrite(DIR2KA,LOW); //DIR2KA output LOW
//  analogWrite(PWMKA,kec);   //PWMKA output kec
//}
//
//void kiriMundur(unsigned char kec)
//{
//  digitalWrite(DIR1KI,HIGH);
//  digitalWrite(DIR2KI,LOW);
//  analogWrite(PWMKI,kec);
//}
//
//void kananRem()
//{
//  digitalWrite(DIR1KA,HIGH);//DIR1KA output HIGH
//  digitalWrite(DIR2KA,HIGH);//DIR2KA output HIGH
//  analogWrite(PWMKA,255);   //PWMKA output 255
//}
//
//void kiriRem()
//{
//  digitalWrite(DIR1KI,HIGH);
//  digitalWrite(DIR2KI,HIGH);
//  analogWrite(PWMKI,255);
//}
//
//void Maju(unsigned char kecKiri,unsigned char kecKanan)
//{
//  kananMaju(kecKanan);
//  kiriMaju(kecKiri); 
//}
//
//void Mundur(unsigned char kecKiri,unsigned char kecKanan)
//{
//  kananMundur(kecKanan);
//  kiriMundur(kecKiri); 
//}
//
//void Belka(unsigned char kecKiri,unsigned char kecKanan)
//{
//  kananMundur(kecKanan);
//  kiriMaju(kecKiri); 
//}
//
//void Belki(unsigned char kecKiri,unsigned char kecKanan)
//{
//  kananMaju(kecKanan);
//  kiriMundur(kecKiri); 
//}
//
//void Rem()
//{
//  kananRem();
//  kiriRem(); 
//}
//
//void cekMotor()
//{
//  Maju(100,100); //robot maju
//  delay(1000); Rem(); delay(500); //robot ngerem
//  Mundur(100,100); //robot mundur
//  delay(1000); Rem(); delay(500); //robot ngerem
//  Belka(100,100); // robot belok kanan
//  delay(1000); Rem(); delay(500); //robot ngerem
//  Belki(100,100); // robot belok kiri
//  delay(1000); Rem(); delay(500); //robot ngerem
//}
