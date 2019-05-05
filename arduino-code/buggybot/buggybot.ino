/**************************************************************************************************
** includes & target selection
**************************************************************************************************/
  #include <Timer.h>                  // A rather useful timer library to offset the comms loop requirement to the motion requirement
  #include <SymmetrySerial.h>         // My balanced protocol
  #include <AltSoftSerial.h>          // virtual serial port

  AltSoftSerial mySerial; // RX, TX
  SymmetrySerial bluetoothComms(&mySerial, 9600, 5000);
/**************************************************************************************************
** runtime basic vars
**************************************************************************************************/


  int heartbeatCurrentState = 2;
  unsigned long lastHeartbeat = 5000;

  Timer motionTimer;
  unsigned long lastLoop = millis();
  unsigned long lastMove = millis();

  //stats counters 
  uint16_t count = 0;
  uint16_t heartbeatCount = 0;
  uint16_t failedMessageCount = 0;
  uint16_t successMessageCount = 0;

  //message buffers
  char outputMessage[250];

  // debugging?
  bool debug = false;

  // blink timer
  int blinkState = LOW;
  Timer blinkTimer;
  int blinkTimerId = 0;
  int blinkLed = 13;

  //the loop time in MS - no need to change this really
  uint16_t loopTime = 5;

/**************************************************************************************************
** Modules and items
**************************************************************************************************/
  /*** drive commands ***/
  #define FEATURE_DRIVE 0x10
  #define FEATURE_DRIVE_FORWARD       FEATURE_DRIVE + 0x01
  #define FEATURE_DRIVE_REVERSE       FEATURE_DRIVE + 0x02
  #define FEATURE_DRIVE_ANTICLOCKWISE FEATURE_DRIVE + 0x03
  #define FEATURE_DRIVE_CLOCKWISE     FEATURE_DRIVE + 0x04
  #define FEATURE_STOP 0x20

  /*** exec commands ***/
  #define FEATURE_EXEC 0x30
  #define FEATURE_EXEC_1 FEATURE_EXEC + 0x01
  #define FEATURE_EXEC_2 FEATURE_EXEC + 0x02
  #define FEATURE_EXEC_3 FEATURE_EXEC + 0x03
  #define FEATURE_EXEC_4 FEATURE_EXEC + 0x04
  #define FEATURE_EXEC_5 FEATURE_EXEC + 0x05
  #define FEATURE_EXEC_6 FEATURE_EXEC + 0x06
  #define FEATURE_EXEC_7 FEATURE_EXEC + 0x07
  #define FEATURE_EXEC_8 FEATURE_EXEC + 0x08

/*************************************************************************************************
** runtime, stats and configuration defines, loop, startup and setup methods
**************************************************************************************************/

  // motor one
  int enA = 3;
  int in1 = 2;
  int in2 = 7;
  // motor two
  int enB = 6;
  int in3 = 5;
  int in4 = 4;

  void setup(void) {
    // start up the serial ports
    // mySerial.begin(9600);
    startupSetupSerialAndTimers();
    pinMode(blinkLed, OUTPUT);


    // do a loop so we're ready to rock
    lastHeartbeat = millis();

    //set up the hbridge and make the motors not move
    pinMode(enA, OUTPUT);
    pinMode(enB, OUTPUT);
    pinMode(in1, OUTPUT);
    pinMode(in2, OUTPUT);
    pinMode(in3, OUTPUT);
    pinMode(in4, OUTPUT);

    commandReceivedStop();
  }

  void loop() {
    // Any messages for me?
    bluetoothComms.poll();
    updateTimers();
    updateStats();
    
    // always need a delay, even if it's a little one
    delay(loopTime);

  }

  void updateTimers() {
    motionTimer.update();
    blinkTimer.update();
  }

  /** Start up the serial ports */
  void startupSetupSerialAndTimers() {
    // Debugging serial port setup
    Serial.begin(9600);

    bluetoothComms.setCallBacks(ProcessComMessage, recieveStatusMessage);

    // Bluetooth Comms connect
    bluetoothComms.connect();

    // Setup the motion timer
    blinkTimerId = blinkTimer.every(500, blinkArduinoLED);
  }

  void blinkArduinoLED() {
    blinkState = (blinkState == HIGH? LOW : HIGH);
    digitalWrite(blinkLed, blinkState);
  }

/**************************************************************************************************
** Comms section, process received messages
**************************************************************************************************/
  void recieveStatusMessage(uint8_t message) {
    switch (message) {
      case HELO:
        heartbeatCount++;
        bluetoothComms.sendStatusACK();
        if (debug) echoToSerial("Heartbeat received");
        break;
      case STATUS_DEBUG_ON:
        debug = true;
        if (debug) echoToSerial("STATUS_DEBUG_ON received");
        break;
      case STATUS_DEBUG_OFF:
        debug = false;
        if (debug) echoToSerial("STATUS_DEBUG_OFF received");
        break;
    }
    lastHeartbeat = millis();
  }

  void ProcessComMessage() {
    bool success = true;

    if (debug) {
      sprintf(outputMessage, "size %d set %d feature %d", bluetoothComms.getReceiveLength(), bluetoothComms.getReceiveFeatureSet(), bluetoothComms.getReceiveFeature());
      echoToSerial(outputMessage);
    }

    lastHeartbeat = millis();

    switch (bluetoothComms.getReceiveFeatureSet()) {
      case FEATURE_STOP:
        commandReceivedStop();
        break;
      case FEATURE_DRIVE:
        commandReceivedDrive();
        break;
      case FEATURE_EXEC:
        commandReceivedExec();
        break;
      default:
        success = false;
        break;
    }
    if (success) {
      successMessageCount ++;
    }
    else {
      failedMessageCount ++;
    }
  }

  // Update the stats counters (only loops at this stage)
  void updateStats() {
    //increment loop time
    lastLoop = millis();

    //increment the loop counter
    count++;
  }

  void echoToSerial(const char* message) {
    Serial.println(message);
  }


/**************************************************************************************************
** doing the actual stuff section
**************************************************************************************************/
  void commandReceivedStop() {
    analogWrite(enA, 0);
    analogWrite(enB, 0);

    digitalWrite(in1, LOW);
    digitalWrite(in2, LOW);  
    digitalWrite(in3, LOW);
    digitalWrite(in4, LOW);
  }
  
  void commandReceivedDrive() {
    byte power = bluetoothComms.getReceiveDataAt(0); // 0 - 100 value
    switch (bluetoothComms.getReceiveFeature())
    {
      case FEATURE_DRIVE_FORWARD:
        //do something driverish in a fairly forwardish way
        if (debug) {
          sprintf(outputMessage, "FEATURE_DRIVE_FORWARD @ %d", power);
          echoToSerial(outputMessage);
        }
        moveMotors(true, true, power, power);
        break;
      case FEATURE_DRIVE_REVERSE:
        //do something driverish in a kinda reversish way
        if (debug) {
          sprintf(outputMessage, "FEATURE_DRIVE_REVERSE @ %d", power);
          echoToSerial(outputMessage);
        }
        moveMotors(false, false, power, power);
        break;
      case FEATURE_DRIVE_ANTICLOCKWISE:
        //do something driverish in a roughly anticlockwise direction
        if (debug) {
          sprintf(outputMessage, "FEATURE_DRIVE_ANTICLOCKWISE @ %d", power);
          echoToSerial(outputMessage);
        }
        moveMotors(false, true, power, power);
        break;
      case FEATURE_DRIVE_CLOCKWISE:
        //do something driverish in a generally clockwise direction
        if (debug) {
          sprintf(outputMessage, "FEATURE_DRIVE_CLOCKWISE @ %d", power);
          echoToSerial(outputMessage);
        }
        moveMotors(true, false, power, power);
        break;
    }
  }
  int left_map_low = 0;
  int left_map_high = 130; // 0-100 is translated to 0-76 (100 * (100/130)))
  int right_map_low = 0;
  int right_map_high = 100;

  void moveMotors(bool leftFwd, bool rightFwd, int leftSpeed, int rightSpeed) {
    if (leftFwd) {
      digitalWrite(in1, HIGH);
      digitalWrite(in2, LOW);
    }
    else {
      digitalWrite(in1, LOW);
      digitalWrite(in2, HIGH);  
    }

    if (rightFwd) {
      digitalWrite(in3, HIGH);
      digitalWrite(in4, LOW);
    }
    else {
      digitalWrite(in3, LOW);
      digitalWrite(in4, HIGH); 
    }

    int leftSpeedByte = map(leftSpeed, left_map_low, left_map_high, 0, 255);
    int rightSpeedByte = map(rightSpeed, right_map_low, right_map_high, 0, 255);
    analogWrite(enA, leftSpeedByte);
    analogWrite(enB, rightSpeedByte);

  }

  void commandReceivedExec() {
    switch (bluetoothComms.getReceiveFeature()) {
      case FEATURE_EXEC_1:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_1");
        break;
      case FEATURE_EXEC_2:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_2");
        break;
      case FEATURE_EXEC_3:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_3");
        break;
      case FEATURE_EXEC_4:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_4");
        break;
      case FEATURE_EXEC_5:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_5");
        break;
      case FEATURE_EXEC_6:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_6");
        break;
      case FEATURE_EXEC_7:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_7");
        break;
      case FEATURE_EXEC_8:
        //do something featury
        if (debug) echoToSerial("called FEATURE_EXEC_8");
        break;
    }
  }
