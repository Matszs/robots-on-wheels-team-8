void setup() {
  Serial.begin(9600);
}

void loop() {
  int i;
  int ir1 = 0;
  int ir2 = 0;
  int ir3 = 0;
  int ir4 = 0;

  for (i=0; i<10; i++){
    ir1 += analogRead(A0);
    ir2 += analogRead(A1);
    ir3 += analogRead(A2);
    ir4 += analogRead(A3);

    delay(1);
  }
  
  ir1 /= 10;
  ir2 /= 10;
  ir3 /= 10;
  ir4 /= 10;

  Serial.print("\x02");
  Serial.print(ir1);
  Serial.print(",");
  Serial.print(ir2);
  Serial.print(",");
  Serial.print(ir3);
  Serial.print(",");
  Serial.print(ir4);
  Serial.print("\x03");
 
}
