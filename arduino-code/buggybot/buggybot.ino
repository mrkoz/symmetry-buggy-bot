/**************************************************************************************************
** includes & target selection
**************************************************************************************************/
  #include <Timer.h>                  // A rather useful timer library to offset the comms loop requirement to the motion requirement
  #include <SymmetrySerial.h>         // My balanced protocol
  #include "comms.h"                  // Spider command protocol and general stuff

/*************************************************************************************************
** runtime, stats and configuration defines, loop, startup and setup methods
**************************************************************************************************/
  SymmetrySerial bluetoothComms(&Serial2, BTBAUDRATE, BTHEARTBEATRATE);

  void setup(void) {
    // start up the serial ports
    startupSetupSerialAndTimers();
    pinMode(blinkLed, OUTPUT);

    // do a loop so we're ready to rock
    lastHeartbeat = millis();
  }

  void loop() {
    //update statistics
    updateStats();

    // Any messages for me?
    bluetoothComms.poll();

    blinkTimer.update();
    
    // hold up - this is only here in case of 
    if (loopTime > 0) {
      delay(loopTime);
    }
  }

  /** Start up the serial ports */
  void startupSetupSerialAndTimers() {
    // Debugging serial port setup
    Serial.begin(SERIALBAUDRATE);

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
        break;
      case STATUS_DEBUG_ON:
        echoToSerial("STATUS_DEBUG_ON received");
        debug = true;
        break;
      case STATUS_DEBUG_OFF:
        echoToSerial("STATUS_DEBUG_OFF received");
        debug = false;
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
      successMessageCount++;
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
    echoToSerial("called STOP");
  }
  
  void commandReceivedDrive() {
    echoToSerial("drive");
    byte power = bluetoothComms.getReceiveDataAt(0); // 0 - 100 value
    switch (bluetoothComms.getReceiveFeature())
    {
      case FEATURE_DRIVE_FORWARD:
        //do something driverish in a fairly forwardish way
        sprintf(outputMessage, "FEATURE_DRIVE_FORWARD @ %d", power);
        echoToSerial(outputMessage);
        break;
      case FEATURE_DRIVE_REVERSE:
        //do something driverish in a kinda reversish way
        sprintf(outputMessage, "FEATURE_DRIVE_REVERSE @ %d", power);
        echoToSerial(outputMessage);
        break;
      case FEATURE_DRIVE_ANTICLOCKWISE:
        //do something driverish in a roughly anticlockwise direction
        sprintf(outputMessage, "FEATURE_DRIVE_ANTICLOCKWISE @ %d", power);
        echoToSerial(outputMessage);
        break;
      case FEATURE_DRIVE_CLOCKWISE:
        //do something driverish in a generally clockwise direction
        sprintf(outputMessage, "FEATURE_DRIVE_CLOCKWISE @ %d", power);
        echoToSerial(outputMessage);
        break;
    }
  }

  void commandReceivedExec() {
    switch (bluetoothComms.getReceiveFeature()) {
      case FEATURE_EXEC_1:
        //do something featury
        echoToSerial("called FEATURE_EXEC_1");
        break;
      case FEATURE_EXEC_2:
        //do something featury
        echoToSerial("called FEATURE_EXEC_2");
        break;
      case FEATURE_EXEC_3:
        //do something featury
        echoToSerial("called FEATURE_EXEC_3");
        break;
      case FEATURE_EXEC_4:
        //do something featury
        echoToSerial("called FEATURE_EXEC_4");
        break;
      case FEATURE_EXEC_5:
        //do something featury
        echoToSerial("called FEATURE_EXEC_5");
        break;
      case FEATURE_EXEC_6:
        //do something featury
        echoToSerial("called FEATURE_EXEC_6");
        break;
      case FEATURE_EXEC_7:
        //do something featury
        echoToSerial("called FEATURE_EXEC_7");
        break;
      case FEATURE_EXEC_8:
        //do something featury
        echoToSerial("called FEATURE_EXEC_8");
        break;
    }
  }
