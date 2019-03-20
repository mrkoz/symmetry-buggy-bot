package ninja.symmetry.robobuggy.serial;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;

/**
 * Created by koz on 17/07/2014.
 */
interface iSERDevice { // extends SerialPortEventListener {
    /*  Section: Members */
    /**
     * Collection of event listeners TODO: make this work
     */
    /* Properties */

    boolean isConnected();
    boolean isDisconnected();
    /* methods */

    /**
     * Heartbeat thread to watch over the other threads
     */
    void startHeartBeatThread();

    /**
     *  Stop the Heartbeat thread - used when disconnect(ing/ed)
     */
    void stopHeartBeatThread();

    /**
     * scans through the serial devices on this platform, stores it locally and starts it up
     * @return boolean Found Device
     */
    boolean discoverSerialDevice();

    /**
     * Adds a command onto the queue for sending
     * @param command SERpacket to send
     */
    void enqueueCommand(SERPacket command);

    /**
     * Connects to the found device
     * @throws Exception if not found
     */
    void connect() throws Exception;



    /* Section: sending information */
    /**
     * Sends the command requested
     * @param value byte[] : full command
     */
    void sendBytes(byte[] value);

    /**
     * Decodes command and fire's off the received event if information is complete
     */
    SERPacket decodeCommand(byte input);

    /**
     * clears all the sockets and streams, ready for a disconnect/reconnect
     */
    void clearSocketsAndStreams();
    void stopReceiveThread();
    void stopSendThread();
}
