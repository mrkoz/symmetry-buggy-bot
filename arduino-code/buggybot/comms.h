/**************************************************************************************************
** Message Defines
**************************************************************************************************/

  /* status messages */
  #define STATUS_DEBUG_ON 0xF0
  #define STATUS_DEBUG_OFF 0xF1
  #define STATUS_POWER_UP 0xF2
  #define STATUS_POWER_DOWN 0xF3
  #define STATUS_RESET_CPU 0xF4
  #define STATUS_RESET_COMMS 0xF5
  #define STATUS_ERASE_EEPROM 0xF6
  #define STATUS_RESET_TO_DEFAULTS 0xF7
  #define STATUS_RESET_ZERO_ONE 0xF8
  #define STATUS_RESET_ZERO_TWO 0xF9
  #define STATUS_AUX 0xFA

/**************************************************************************************************
** Modules and items
**************************************************************************************************/
/*** drive command ***/
  #define FEATURE_DRIVE  0x10

/*** leg tweaks ***/
  #define FEATURE_EXEC   0x20
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
  #define TIMEOUT_HEART_BEAT_WARN 2000
  #define TIMEOUT_HEART_BEAT_ERROR 4000
  #define HEARTBEAT_STATE_GOOD 0
  #define HEARTBEAT_STATE_WARN 1
  #define HEARTBEAT_STATE_ERROR 2
  
  int heartbeatCurrentState = 2;
  unsigned long lastHeartbeat = 5000;