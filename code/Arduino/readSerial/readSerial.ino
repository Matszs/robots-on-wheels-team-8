
void setup() {
  Serial.begin(9600);
}

void loop() {
  Serial.print("IR 1: ");
  Serial.print(analogRead(A0));
  Serial.print(" IR 2: ");
  Serial.print(analogRead(A1));
  Serial.print(" IR 3: ");
  Serial.print(analogRead(A2));
  Serial.print(" IR 4: ");
  Serial.print(analogRead(A3));
  Serial.println();
  
  
  delay(1);
}
