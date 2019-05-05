package ninja.symmetry.robobuggy.serial;

import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;

/**
 * Created by koz on 18/07/2014.
 */
public abstract class SERDevice {
    public static String TAG_CON = "SERCON";

    public final Queue<SERPacket> commandsToSend = new LinkedList<SERPacket>();

    public boolean isConnecting = false;
    public boolean disconnected = false;

    public Date lastRecieved = new Date();

    //queues and streams
    public Queue<Byte> receiveQueue = new LinkedList<Byte>();
    public InputStream iStream;
    public OutputStream oStream;

    //heartbeat thread and parts
    protected Thread threadHeartBeat;
    public boolean threadHeartBeatKill = false;
    public int threadHeartBeatWaitTime = 5000;
    public int threadHeartBeatDieTime = 20000;

    //send thread and parts
    protected Thread threadSend;
    public boolean threadSendKill = false;
    public int threadSendWaitTime = 50;
    public int threadSendGapTime = 40;

    //receive thread and parts
    protected Thread threadReceive;
    public boolean threadReceiveKill = false;
    public int threadReceiveWaitTime = 80;


    /*  Section: Members */

    /* Properties */
    public boolean isConnected() {
        return false;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    /* Section: sending information */
    public void enqueueCommand(SERPacket command) {

        synchronized (commandsToSend) {
            commandsToSend.add(command);
            commandsToSend.notifyAll();
        }
    }

    /**
     * Sends the command requested
     * @param value byte[] : full command
     */
    public void sendBytes(byte[] value) {
        try {
            this.oStream.write(value);
        }
        catch (IOException e) {
            this.threadHeartBeatKill = true;
            Log.i(TAG_CON, "error sending bytes");
        }
    }

    /**
     * Heartbeat thread to watch over the other threads
     */
    public void startHeartBeatThread() {
        if (threadHeartBeat == null || !threadHeartBeat.isAlive()) {
            threadHeartBeat = new Thread() {
                public void run() {
                    try {
                        while (!threadHeartBeatKill) {
                            if (!isConnected()) {
                                Log.i(TAG_CON, "not connected, killing");
                                // Broadcast device disconnected message
//                                Intent intent = new Intent(App.get().getString(R.string.ser_device_disconnected));

                                threadHeartBeatKill = true;
                                stopReceiveThread();
                                stopSendThread();
                            }
                            else {
                                if (threadReceive == null || !threadReceive.isAlive() || !isConnected()) {
                                    Log.i(TAG_CON, "restarting the receive thread");
                                    stopReceiveThread();
                                    startReceiveThread();
                                }
                                if (threadSend == null || !threadSend.isAlive() || !isConnected()) {
                                    Log.i(TAG_CON, "restarting the send thread");
                                    stopSendThread();
                                    startSendThread();
                                }
                                long interval = (new Date()).getTime() - lastRecieved.getTime();
                                if (interval > threadHeartBeatDieTime) {
                                    disconnected = true;
                                    Log.i(TAG_CON, "heartbeat dead");
                                    resetConnection();
                                    break;
                                }
                                else
                                if (interval > threadHeartBeatWaitTime) {
                                    sendHelloMessage();
                                    Log.i(TAG_CON, "heartbeat");
                                }
                                sleep(threadHeartBeatWaitTime);
                            }
                        }
                        threadHeartBeatKill = false;
                    }
                    catch (InterruptedException e) {
                        Log.i(TAG_CON, "threadHeartBeat thread died, it was interrupted!");
//                        Intent intent = new Intent(App.get().getString(R.string.ser_device_disconnected));
//                        LocalBroadcastManager.getInstance(App.get()).sendBroadcast(intent);
                        startHeartBeatThread();
                    }
                    catch (Exception e) {
                        Log.i(TAG_CON, "threadHeartBeat thread died for an unknown reason! " + e.getLocalizedMessage());
//                        Intent intent = new Intent(App.get().getString(R.string.ser_device_disconnected));
//                        LocalBroadcastManager.getInstance(App.get()).sendBroadcast(intent);
                        resetConnection();
                    }
                }
            };
            threadHeartBeat.start();
        }
    }

    /**
     * Clear all the streams and sockets
     */

    public void clearSocketsAndStreams() throws Exception{
    }


    /**
     *  Stop the Heartbeat thread - used when disconnect(ing/ed)
     */
    public void stopHeartBeatThread() {
        threadHeartBeatKill = true;
    }



    /**
     * Stop the receive thread.
     */
    public void stopReceiveThread() {
        threadReceiveKill = true;
    }

    /**
     * Reset connection
     */
    public void resetConnection() {
        try {
            stopSendThread();
            stopReceiveThread();
            stopHeartBeatThread();
            clearSocketsAndStreams();
            SERSerial.mHandler.connectionStateChanged(false);
        }
        catch (Exception e) {
            Log.i(TAG_CON, "reset connection thread exception " + e.getLocalizedMessage());

        }
    }

    /**
     * Starts the receive thread.
     */
    public void startReceiveThread() {
        Log.i(TAG_CON, "Receive thread setup");

        threadReceive = new Thread() {
            public void run() {
                try {
                    threadReceiveKill = false;

                    while (!threadReceiveKill) {
                        receiveData();
                        sleep(threadReceiveWaitTime);
                    }
                }
                catch (IOException e) {
                    resetConnection();
                }
                catch (InterruptedException e) {
                    Log.i(TAG_CON, "receive thread interrupted while sleeping");
                }
                catch (Exception e) {
                    Log.i(TAG_CON, "receive thread crashed for some reason" + e.getLocalizedMessage());
                }
            }
        };
        threadReceive.start();
    }

    /**
     * Stop the send thread.
     */
    public void stopSendThread() {
        threadSendKill = true;
    }

    /**
     * Starts the send thread.
     */
    public void startSendThread() {
        Log.i(TAG_CON, "Send thread setup");

        threadSend = new Thread() {
            public void run() {
                try {
                    threadSendKill = false;
                    while (!threadSendKill) {
                        synchronized (commandsToSend) {
                            while (commandsToSend.iterator().hasNext()) {
                                SERPacket toSend = commandsToSend.remove();
                                byte[] commandToSend = toSend.buildCommand();
                                Log.i(TAG_CON, "sending: " + toSend.toString());
                                // TODO: integrate this into the ack/nack loop
                                sendBytes(commandToSend);
                                sleep(threadSendGapTime);
                            }
                        }
                        sleep(threadSendWaitTime);
                    }
                }
                catch (InterruptedException e) {
                    Log.i(TAG_CON, "send thread interrupted while sleeping" + e.getLocalizedMessage());
                }
                catch (Exception e) {
                    Log.i(TAG_CON, "send thread crashed for some reason:" + e.getLocalizedMessage());
                }
            }
        };
        threadSend.start();
    }


    /**
     * Detects if there is a complete command
     * @return hasCommand
     */
    public void receiveData() throws IOException {
        try {
            byte[] buffer = new byte[1];

            while (iStream.read(buffer, 0, 1) > 0) {
                SERPacket packet = this.decodeCommand(buffer[0]);
                this.lastRecieved = new Date();
                if (packet != null) {
                    Log.i(TAG_CON, "Received packet: " + packet);

                    if (SERSerial.mHandler != null) {
                        Message msg = Message.obtain();
                        msg.obj = packet;
                        SERSerial.mHandler.sendMessage(msg);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }


    /**
     * Decodes command and fire's off the received event if information is complete
     */
    public SERPacket decodeCommand(byte input) {
        receiveQueue.add(input);
        return SERPacket.fromInputQueue(receiveQueue);
    }

    /**
     * sends a helo message
     */
    public void sendHelloMessage() {
        SERSerial ser = SERSerial.getInstance();
        synchronized (ser) {
            ser.sendMessage(new SERPacket(SERPacket.HELO));
        }
    }
}
