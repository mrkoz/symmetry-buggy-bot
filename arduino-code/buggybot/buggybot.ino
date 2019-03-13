/**************************************************************************************************
** includes & target selection
**************************************************************************************************/
  #include <Timer.h>                  // A rather useful timer library to offset the comms loop requirement to the motion requirement
  #include <SymmetrySerial.h>         // My balanced protocol
  #include "comms.h"         // Spider command protocol and general stuff

/*************************************************************************************************
** runtime, stats and configuration defines, loop, startup and setup methods
**************************************************************************************************/
  SymmetrySerial bluetoothComms(&Serial3, BTBAUDRATE, BTHEARTBEATRATE);
  
  Timer motionTimer;
  unsigned long lastLoop = millis();
  unsigned long lastMove = millis();

  //stats counters 
  uint16_t count = 0;
  uint16_t heartbeatCount = 0;
  uint16_t failedMessageCount = 0;
  uint16_t successMessageCount = 0;
  int movementCyclesCount = 0;
  int servoMovementsCount = 0;
  unsigned long lastContinue = 0;

  //message buffers
  char outputMessage[250];

  // debugging?
  bool debug = false;

  //the loop time in MS
  uint16_t loopTime = 5;
  uint8_t motiontimerid = 0;
  uint8_t uitimerid = 0;

  void setup(void) {   
    // start up the serial ports
    startupSetupSerial();

    // load the settings and profiles
    startupLoadSettingsAndProfiles();

    // startup the timers
    startupStartTimers();

    // do a loop so we're ready to rock
    lastHeartbeat = millis();
    loop();
  }

  void loop() {
    //update statistics
    updateStats();

    // Any messages for me?
    bluetoothComms.poll();
    
    // hold up - this is only here in case of 
    if (loopTime > 0) {
      delay(loopTime);
    }
  }

  /** Start up the serial ports */
  void startupSetupSerial() {
    // Debugging serial port setup
    Serial.begin(SERIALBOUDRATE);
    // Bluetooth Comms setup
    bluetoothComms.setCallBacks(ProcessComMessage, recieveStatusMessage);
    bluetoothComms.connect();
  }


  /** Startup the timers */
  void startupStartTimers() {
    // Setup the motion timer
    motiontimerid = motionTimer.every(walkPhaseTime, servoContinue);
    // Setup the UI update timer
    uitimerid = uiDebugTimer.every(TIMEOUT_UI_REFRESH, updateUiDebugMessages);
  }

/**************************************************************************************************
** Comms section, process received messages
**************************************************************************************************/
  void recieveStatusMessage(uint8_t message) {
    switch (message) {
      case HELO:
        if (debug) {
          echoToSerial("HELO, sending ACK");
        }
        bluetoothComms.sendStatusACK();
        heartbeatCount++;
        break;
      case ACK:
        if (debug) {
          echoToSerial("ACK received");
        }
        break;
      case NACK:
        if (debug) {
          echoToSerial("NACK received");
        }
        break;
      case FAIL:
        if (debug) {
          echoToSerial("FAIL received");
        }
        break;
      case STATUS_DEBUG_ON:
        echoToSerial("STATUS_DEBUG_ON received");
        featureTestDumpMinMaxAndStances();
        debug = true;
        #ifdef MODEL1
        startupDrawUxAndFace();
        #endif
        break;
      case STATUS_DEBUG_OFF:
        echoToSerial("STATUS_DEBUG_OFF received");
        debug = false;
        #ifdef MODEL1
        startupDrawUxAndFace();
        #endif
        break;
      case STATUS_POWER_UP:
        if (debug) {
          echoToSerial("STATUS_POWER_UP received");
        }
        break;
      case STATUS_POWER_DOWN:
        if (debug) {
          echoToSerial("STATUS_POWER_DOWN received");
        }
        break;
      case STATUS_RESET_CPU:
        if (debug) {
          echoToSerial("STATUS_RESET_CPU received");
        }
        break;
      case STATUS_RESET_COMMS:
        if (debug) {
          echoToSerial("STATUS_RESET_COMMS received");
        }
        break;
      case STATUS_ERASE_EEPROM:
        if (debug) {
          echoToSerial("STATUS_ERASE_EEPROM received");
        }
        break;
      case STATUS_RESET_TO_DEFAULTS:
        if (debug) {
          echoToSerial("STATUS_RESET_TO_DEFAULTS received");
        }
        idResetMinMaxDefaults();
        idResetStanceSettings();
        resetLegSettings();
        break;
      case STATUS_RESET_ZERO_ONE:
        if (debug) {
          echoToSerial("STATUS_RESET_ZERO_ONE received");
        }
        break;
      case STATUS_RESET_ZERO_TWO:
        if (debug) {
          echoToSerial("STATUS_RESET_ZERO_TWO received");
        }
        break;
      case STATUS_AUX:
        if (debug) {
          echoToSerial("STATUS_AUX received");
        }
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

/**************************************************************************************************
** doing the actual stuff section
**************************************************************************************************/
  void commandReceivedDrive() {
  }

  void commandReceivedExec() {
    switch (bluetoothComms.getReceiveFeature()) {
      case FEATURE_EXEC_1:
        //do something featury      
        break;
      case FEATURE_EXEC_2:
        //do something featury      
        break;
      case FEATURE_EXEC_3:
        //do something featury      
        break;
      case FEATURE_EXEC_4:
        //do something featury      
        break;
      case FEATURE_EXEC_5:
        //do something featury      
        break;
      case FEATURE_EXEC_6:
        //do something featury      
        break;
    }
  }
