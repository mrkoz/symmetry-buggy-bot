/*
  SymmetrySerial.cpp - Library for SymmetrySimple code.
  Created by Adam Purdie, August 2, 2016.
  License as per License.txt
*/
#include "Arduino.h"
#include "SymmetrySerial.h"
#include <AltSoftSerial.h>
#include <SoftwareSerial.h>

/***** Constructors hardware serial *****/
/* Constructor */
SymmetrySerial::SymmetrySerial(HardwareSerial *port, int baudRate) {
  _port = port;
  _baudRate = baudRate;
  _portType = HWPORT;
}

/* Constructor with heartbeat */
SymmetrySerial::SymmetrySerial(HardwareSerial *port, int baudRate, unsigned long heartBeat) {
  _port = port;
  _baudRate = baudRate;
  _heartBeat = heartBeat;
  _portType = HWPORT;
}

/***** Constructors alt software serial *****/
/* Constructor */
SymmetrySerial::SymmetrySerial(AltSoftSerial *port, int baudRate) {
  _port = port;
  _baudRate = baudRate;
  _portType = ALTPORT;
}

/* Constructor with heartbeat */
SymmetrySerial::SymmetrySerial(AltSoftSerial *port, int baudRate, unsigned long heartBeat) {
  _port = port;
  _baudRate = baudRate;
  _heartBeat = heartBeat;
  _portType = ALTPORT;
}

/***** Constructors software serial *****/
/* Constructor */
SymmetrySerial::SymmetrySerial(SoftwareSerial *port, int baudRate) {
  _port = port;
  _baudRate = baudRate;
  _portType = SSPORT;
}

/* Constructor with heartbeat */
SymmetrySerial::SymmetrySerial(SoftwareSerial *port, int baudRate, unsigned long heartBeat) {
  _port = port;
  _baudRate = baudRate;
  _heartBeat = heartBeat;
  _portType = SSPORT;
}

/* set callbacks */
void SymmetrySerial::setCallBacks(void (*callback)(void), void (*statusCallback)(uint8_t message)) {
  _messageCallback = callback;
  _statusCallback = statusCallback;
}

/***** Port connect/disconnect *****/
/* Stop serial port connectivity */
void SymmetrySerial::connect() {
  if (_portType == HWPORT) {
    static_cast<HardwareSerial*>(_port)->begin(_baudRate);
  } 
  else if (_portType == ALTPORT) {
    static_cast<AltSoftSerial*>(_port)->begin(_baudRate);
  }
  else if (_portType == SSPORT) {
    static_cast<SoftwareSerial*>(_port)->begin(_baudRate);
  }
  purgeMessageReceive();
  configured = true;
}

/* Stop serial port connectivity */
void SymmetrySerial::disconnect() {
  if (_portType == HWPORT) {
    static_cast<HardwareSerial*>(_port)->end();
  } 
  else if (_portType == ALTPORT) {
    static_cast<AltSoftSerial*>(_port)->end();
  } 
  else if (_portType == SSPORT) {
    static_cast<SoftwareSerial*>(_port)->end();
  }
  configured = false;
}

/***** State and heartbeat timer functions *****/
/* updates last message and last heartbeat on data received */
void SymmetrySerial::dataReceived() {
  lastMessage = millis();
  heartbeatDead = false;

}

/* checks the heartbeat timeout and fires off a HELO if needed */
void SymmetrySerial::checkheartBeat() {
  if ( _heartBeat > 0 && configured && !heartbeatDead) {
    if ( millis() - lastMessage > _heartBeat && millis() - lastHeartbeat > _heartBeat) {
      sendStatusHELO();
      lastHeartbeat = millis();
    }
    if ( millis() - lastMessage > 5 * _heartBeat ) {
      heartbeatDead = true;
    }
  }
}


/***** data and message methods *****/
/* poll to see if there's new data and process while available */
void SymmetrySerial::poll() {
  checkheartBeat();
  while (configured == true && _port->available() > 0) {
    dataReceived();
    recChar = _port->read() & 0xFF;
    
    if(receiving == 0) {              // if the receiving is set to 0 then we're not currently receiving a message
      if(recChar == MSG_START) {       // check if the recChar is the start char
        receiving = 1;                // starting message
        receiveCount = 0;
      }
      else {
        purgeMessageReceive();               // we got data but no start message? purge
        failedMessageCount++;
      }
    }
    else if (receiving == 1) {
      // we've got a start message and are continuing a message
      receiveChecksum += recChar; // increment the checksum
      switch (receiveCount) {
        case S_SIZE_OR_STATUS:
          if (recChar >= 0xF0) {
            // this is a status message
            statusMessageReceived(recChar);
            (*_statusCallback)(recChar);    // trigger the status callback, clear and exit
            purgeMessageReceive();
          }
          else {
            //this is the data length
            messageReceive.length = recChar;

            //this is also where the counterReceive is reset
            counterReceive = 0x00;
          }
          break;
        case S_FEATURE:
          //this is the feature reference
          messageReceive.feature = recChar;
          break;
        case S_CHECKSUM:
          //this is the checksum char
          messageReceive.checksum = recChar;
          break;
        default:
          // char is part of the data
          if (receiveCount >= SERIAL_MESSAGE_SIZE) {
            messageReceive.dataBuffer[receiveCount - SERIAL_MESSAGE_SIZE] = recChar;
          }
          break;
      }

      if(receiveCount == messageReceive.length + SERIAL_MESSAGE_SIZE - 1) {
        // this was the last packet of data
        if(receiveChecksum == 0) {  // perform checksum and if
          sendStatusACK();
          (*_messageCallback)();
          purgeMessageReceive();
        }
        else {
          sendStatusNACK();
          purgeMessageReceive();
        }
      }

      receiveCount++;
    }
    if (receiving && (millis() - TIMEOUT_SERIAL_BUFFER) > lastMessage) {
      purgeMessageReceive();
      sendStatusNACK();
    }
  }
}

/* receive buffer purge */
void SymmetrySerial::purgeMessageReceive() {
  messageReceive.length = 0x00;
  messageReceive.feature = 0x00;
  messageReceive.checksum = 0x00;
  receiveChecksum = 0x00;
  for(uint8_t i = 0; i < SERIAL_MESSAGE_BUFFER_SIZE; i++) {
    setReceiveDataAt(i, 0xff);
  }
  receiving = 0;
  receiveCount = 0;
}

/* send buffer purge */
void SymmetrySerial::purgeMessageSend() {
  messageSend.length = 0x00;
  messageSend.feature = 0x00;
  messageSend.checksum = 0x00;
  counterSend  = 0x00;
  for (uint8_t i = 0; i < SERIAL_MESSAGE_BUFFER_SIZE; i++) {
    setSendDataAt(i, 0xff);
  }
}

/* set the message length */
void SymmetrySerial::sendSetMessageLength(uint8_t length) {
  messageSend.length = length;
}

/* work's out the value based on the contents - dangerous if there are values = MESSAGE_DATA_BUFFER_BLANK (0xFF) */
void SymmetrySerial::sendSetMessageLengthAuto() {
  for (uint8_t i = 0; i < SERIAL_MESSAGE_BUFFER_SIZE; i++) {
    if (messageSend.dataBuffer[i] == MESSAGE_DATA_BUFFER_BLANK) {
      messageSend.length = i;
      break;
    }
  }
}

/* react to status message */
void SymmetrySerial::statusMessageReceived(uint8_t message) {
  switch (message) {
    case HELO:
      sendStatusACK();
      break;
      //TODO: probably should do something about these
    case NACK:
      break;
    case FAIL:
      break;
  }
}

/* send a status message */
void SymmetrySerial::sendStatusMessage(uint8_t command) {
  if (!configured) return;
  _port->write(0xff);
  _port->write(command);
}

/* pre-canned status messages - HELO */
void SymmetrySerial::sendStatusHELO() {
  sendStatusMessage(HELO); 
}

/* pre-canned status messages - ACK */
void SymmetrySerial::sendStatusACK() {
  sendStatusMessage(ACK); 
}

/* pre-canned status messages - NACK */
void SymmetrySerial::sendStatusNACK() {
  sendStatusMessage(NACK);
}

/* pre-canned status messages - FAIL */
void SymmetrySerial::sendStatusFAIL() {
  sendStatusMessage(FAIL);
}

/* send a message */
void SymmetrySerial::sendMessage() {
  if (!configured) return;

  messageSend.checksum = 256 - (getSendBufferChecksum() % 256);

  _port->write(0xFF);
  _port->write(messageSend.length & 0xFF);
  _port->write(messageSend.feature & 0xFF);
  _port->write(messageSend.checksum & 0xFF);


  for (uint8_t i = 0; i < messageSend.length; i++) {
    _port->write(messageSend.dataBuffer[i] & 0xFF);
  }

  purgeMessageSend();
}

/* quick send for single feature trigger and value */
void SymmetrySerial::sendMessageSingle(uint8_t feature, uint8_t value) {
  purgeMessageSend();
  addByteToSend(value);
  messageSend.feature = feature;
  sendMessage();
}

/* quick send for single feature trigger and value */
void SymmetrySerial::sendMessageSingleWord(uint8_t feature, uint16_t data) {
  purgeMessageSend();
  setSendDataAt(counterSend++, highByte(data) & 0xff);
  setSendDataAt(counterSend++, lowByte(data) & 0xff);
  messageSend.length = counterSend;
  messageSend.feature = feature;
  sendMessage();
}

/* quick send for single feature trigger */
void SymmetrySerial::sendMessageSingle(uint8_t feature) {
  sendMessageSingle(feature, 0);
}

bool SymmetrySerial::is_alive() {
  return (millis() - lastMessage < _heartBeat);
}



/**************************************************************************************************
** Actual serial data commands
**************************************************************************************************/

/* checksum calculation - receive */
uint8_t SymmetrySerial::getRecieveBufferChecksum() {
  uint8_t outcome = messageReceive.length + messageReceive.feature;
  if (messageReceive.length > 0) {
    for (uint8_t i =0; i < messageReceive.length; i++) {
      outcome += getReceiveDataAt(i);
    }
  }
  return outcome;
}
/* set the feature type for the send packet */
void SymmetrySerial::setSendFeature(uint8_t feature) {
  messageSend.feature = feature;
}


/* get the feature set type from the received packet */
uint8_t SymmetrySerial::getReceiveFeatureSet() {
  return (messageReceive.feature - (messageReceive.feature % 0x10));
}

/* get the feature type from the received packet */
uint8_t SymmetrySerial::getReceiveFeature() {
  return messageReceive.feature;
}

/* get the data length of the received packet */
uint8_t SymmetrySerial::getReceiveLength() {
  return messageReceive.length;
}


/* get the checksum of the received packet */
uint8_t SymmetrySerial::getReceiveChecksum() {
  return messageReceive.checksum;
}

/* get the value of the receive dataset at position */
uint8_t SymmetrySerial::getReceiveDataAt(uint8_t position) {
  return messageReceive.dataBuffer[position];
}

/* set the value of the receive dataset at position to value */
void SymmetrySerial::setReceiveDataAt(uint8_t position, uint8_t value) {
  messageReceive.dataBuffer[position] = value;
}

/* checksum calculation - send */
uint8_t SymmetrySerial::getSendBufferChecksum() {
  uint8_t outcome = messageSend.length + messageSend.feature;
  if (messageSend.length > 0) {
    for (uint8_t i =0; i < messageSend.length; i++) {
      outcome += messageSend.dataBuffer[i];
    }
  }
  return outcome;
}

/* get the value of the send dataset at position */
uint8_t SymmetrySerial::getSendDataAt(uint8_t position) {
  return messageSend.dataBuffer[position];
}

/* set the value of the send dataset at position to value */
void SymmetrySerial::setSendDataAt(uint8_t position, uint8_t value) {
  messageSend.dataBuffer[position] = value;
}

/* Add data helpers for send packet */
void SymmetrySerial::addByteToSend(uint8_t data) {
  setSendDataAt(counterSend++, data & 0xFF);
  messageSend.length = counterSend;
}

void SymmetrySerial::addWordToSend(uint16_t data) {
  setSendDataAt(counterSend++, highByte(data));
  setSendDataAt(counterSend++, lowByte(data));
  messageSend.length = counterSend;
}

void SymmetrySerial::resetSendDataCounter() {
  setSendDataCounterTo(0);
}

void SymmetrySerial::setSendDataCounterTo(uint8_t value) {
  counterSend = value;
}

/* Get data from receive packet */
uint8_t SymmetrySerial::getByteFromReceive() {
  return getReceiveDataAt(counterReceive++);
}

uint16_t SymmetrySerial::getWordFromReceive() {
  uint8_t msb = getReceiveDataAt(counterReceive++);
  uint8_t lsb = getReceiveDataAt(counterReceive++);
  return ( msb << 8 | lsb);
}

void SymmetrySerial::resetReceiveDataCounter() {
  setReceiveDataCounterTo(0);
}

void SymmetrySerial::setReceiveDataCounterTo(uint8_t value) {
  counterReceive = value;
}

