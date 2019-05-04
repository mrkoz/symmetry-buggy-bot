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
  uint16_t loopTime = 100;

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

/**************************************************************************************************
** Timers etc
**************************************************************************************************/
  /* heartbeat state defines */
  #define TIMEOUT_HEART_BEAT_WARN 2000
  #define TIMEOUT_HEART_BEAT_ERROR 4000
  #define HEARTBEAT_STATE_GOOD 0
  #define HEARTBEAT_STATE_WARN 1
  #define HEARTBEAT_STATE_ERROR 2

  #define SERIALBAUDRATE 9600
  #define BTBAUDRATE 9600 // Baud rate of the serial port
  #define BTHEARTBEATRATE 2000 //send a helo every 2 seconds
