void cekBluetooth()
{
  mySerial.listen();
  while (mySerial.available() > 0) {
    char inByte = mySerial.read();
    Serial.println(inByte);
  }
}

void play()
{
 msg = "";
 char inByte;
 do{
  while (mySerial.available() <= 0) {}
  inByte = mySerial.read();
  msg += inByte;
 }while(inByte != '/');
 Serial.println(msg);
 mySerial.println(msg);
}
