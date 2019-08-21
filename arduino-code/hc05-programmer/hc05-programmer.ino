
void setup() 
{
  Serial2.begin(9600);
  Serial.begin(9600);
  delay(5000);
}


void loop()                 
{
  Serial.println("LOOP");
  Serial2.println("AT"); // send character A to PC

  char incomingChar;
  while (Serial2.available() > 0) {
    // read the incoming byte:
    incomingChar = Serial2.read();

    // say what you got:
    Serial.print(incomingChar);
  }
  Serial.println();

  delay(5000);


}
