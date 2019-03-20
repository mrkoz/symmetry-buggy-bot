package ninja.symmetry.robobuggy.serial;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.List;

import ninja.symmetry.robobuggy.iMessageReceivableActivity;
import ninja.symmetry.robobuggy.serial.Packets.SERPacket;
import ninja.symmetry.robobuggy.serial.Packets.SERPacketCommand;
import ninja.symmetry.robobuggy.serial.Packets.SERPacketCommandWithData;
import ninja.symmetry.robobuggy.serial.Packets.SERPacketCommandWithSingle;
import ninja.symmetry.robobuggy.R;

import static ninja.symmetry.robobuggy.serial.SERDevice.TAG_CON;

/**
 * Created by koz on 14/07/2014.
 * Serial communication main class
 */
public class SERSerial {
    /* members */
    private static SERSerial _instance;
    private Thread connectionDetectionThread;
    private boolean killConnectionDetectionThread = false;
    private static final int connectionDetectionWaitTime = 300;
    private iSERDevice serialDevice;
    static SERReceiveHandler mHandler;

    /**
     * Gets the application context - useful when you don't have a direct line to an activity
     * @return Application (context) main application
     * @throws Exception
     */

    public static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication").invoke(null, (Object[]) null);
    }

    /**
     * Gets the serial device name for the target spider's bluetooth device
     * @return String
     */
    public static String get_device_name() {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SERSerial.getApplicationUsingReflection());
            return preferences.getString(SERSerial.getApplicationUsingReflection().getString(R.string.serial_port_name), "");
        }
        catch (Exception ex) {
            Log.i("SerialConfig", String.format("Error retrieving preferences %s", ex.getLocalizedMessage()));
        }
        return "";
    }

    public static boolean getConnectedStatus() {
        if (SERSerial.getInstance().serialDevice != null)
            return SERSerial.getInstance().serialDevice.isConnected();
        return false;
    }

    /**
     * sets the preferences name value for the target spider's bluetooth device
     * @param portName
     */
    public static void set_device_name(String portName) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SERSerial.getApplicationUsingReflection());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SERSerial.getApplicationUsingReflection().getString(R.string.serial_port_name), portName);
            editor.commit();
            SERSerial.getInstance().resetConnection();
        }
        catch (Exception ex) {
            Log.i("SerialConfig", String.format("Error retrieving preferences %s", ex.getLocalizedMessage()));
        }

    }

    /* Section: com send and receive */
    /**
     * Enqueues a message to be sent
     * @param message SERPacket to send.
     */
    public void sendMessage(SERPacket message) {
        if (serialDevice != null && serialDevice.isConnected()) {
            serialDevice.enqueueCommand(message);
        }
    }

    public void addActivityToHandler(iMessageReceivableActivity activity) {
        mHandler.addActivity(activity);
    }

    public void removeActivityToHandler(iMessageReceivableActivity activity) {
        mHandler.removeActivity(activity);
    }

    public void sendMessage(int item) {
        this.sendMessage(new SERPacketCommand(item));
    }

    public void sendMessageSingle(int item, int value) {
        this.sendMessage(new SERPacketCommandWithSingle(item, value));
    }

    public void sendMessageWithData(int item, List<Integer> value) {
        this.sendMessage(new SERPacketCommandWithData(item, value));
    }

    /**
     * Singleton to stop views chopping all my code.
     * SERSerial instance
     */
    public static SERSerial getInstance()
    {
        if (_instance == null)
        {
            _instance = new SERSerial();
        }
        return _instance;
    }

    public SERSerial() {
        super();
        try {
            if (serialDevice == null) {
                this.startConnectionDetection();
            }
        } catch (Exception e) {
            Log.i(TAG_CON, "connection or threads failed: " + e.getLocalizedMessage());
        }
    }

    /* Section: connection detection etc */
    /**
     * Heartbeat thread to watch over the other threads TODO: make this work
     */
    private void startConnectionDetection() {
        if (this.connectionDetectionThread == null || !this.connectionDetectionThread.isAlive()) {
            connectionDetectionThread = new Thread() {
                public void run() {
                    try {
                        while (!killConnectionDetectionThread) {
                            try {
                                if (serialDevice == null) {
                                    if (SERSerial.get_device_name() == "") {
                                        Log.i(TAG_CON, "Portname not set!!!");
                                        sleep(500);
                                    }
                                    else {
                                        serialDevice = firstSerialDevice();
                                    }
                                }
                                if (serialDevice != null && serialDevice.isDisconnected()) {
                                    Log.i(TAG_CON, "connection detection - disconnected");

                                    try {
                                        serialDevice.clearSocketsAndStreams();
                                        serialDevice = null;
                                    } catch (Exception e) {
                                        Log.i(TAG_CON, " connection detection caught exception at clear sockets and streams" + e.getLocalizedMessage());
                                    }
                                }
                                if (serialDevice != null && !serialDevice.isConnected()) {
                                    Log.i(TAG_CON, "connection detection - not connected, attempting to connect");
                                    serialDevice.clearSocketsAndStreams();
                                    serialDevice.connect();
                                }
                            }
                            catch (Exception e) {
                                Log.i(TAG_CON, "connection detection thread crashed for an unknown reason!" + e.getLocalizedMessage());
                                if (serialDevice != null) {
                                    serialDevice.clearSocketsAndStreams();
                                }
                            }
                            sleep(connectionDetectionWaitTime);
                        }
                    }
                    catch (InterruptedException e) {
                        Log.i(TAG_CON, "connection detection thread died, it was interrupted!");
                        startConnectionDetection();
                    }
                    catch (Exception e) {
                        Log.i(TAG_CON, "connection detection thread crashed for an unknown reason!" + e.getLocalizedMessage());
                        startConnectionDetection();
                    }

                }
            };
            connectionDetectionThread.start();
        }
    }


    /* Section: connectivity */
    /**
     * Find the first device
     * @return SERDevice the first confirmed device it finds
     */
    private iSERDevice firstSerialDevice() {
        SERDeviceBluetooth bluetooth = new SERDeviceBluetooth();
        try {
            bluetooth.getBluetoothAdapter();
        }
        catch (Exception e) {
            Log.i(TAG_CON, "couldn't find bluetooth adaptor");
        }
        if (bluetooth.discoverSerialDevice()) {
            return bluetooth;
        }
        return null;
    }

    public void resetConnection() {
        if (serialDevice != null) {
            serialDevice.stopSendThread();
            serialDevice.stopReceiveThread();
            serialDevice.stopHeartBeatThread();
            serialDevice.clearSocketsAndStreams();

        }
        serialDevice = null;
    }
}
