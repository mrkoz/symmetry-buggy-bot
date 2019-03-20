package ninja.symmetry.robobuggy.serial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


/**
 * Created by koz on 16/07/2014.
 * Refs
 * //http://developer.android.com/reference/android/bluetooth/BluetoothSocket.html
 */
public class SERDeviceBluetooth extends SERDevice implements iSERDevice {
    /* Section: Members */
    public BluetoothSocket btSocket;
    public BluetoothAdapter btAdapter;
    public BluetoothDevice btDevice;
    public static String CON_BLUETOOTH_SERIAL_PORT_UUID = "00001101-0000-1000-8000-00805F9B34FB";


    /**
     * gets the adapter
     *
     * @throws Exception if there isn't one!
     */
    public void getBluetoothAdapter() throws Exception {
        if (this.btAdapter == null) {
            this.btAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.i(TAG_CON, "got adapter " + btAdapter.getName());
        }
    }

    /**
     * scans through the serial devices on this platform, stores it locally and starts it up
     *
     * @return boolean Found Device
     */
    public boolean discoverSerialDevice() {
        try {
            if (SERSerial.get_device_name() == "") {
                throw new Exception("Portname not set!!!");
            }
            if (btDevice != null && btDevice.getName() != null) {
                return true;
            }
            Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
            Log.i(TAG_CON, String.format("Devices found %d", devices.size()));

            for (int i = 0; i < devices.size(); i++) {

                BluetoothDevice curDev = (BluetoothDevice) devices.toArray()[i];
                Log.i(TAG_CON, curDev.getName());
                if (curDev.getName().equals(SERSerial.get_device_name())) {
                    btDevice = curDev;
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            Log.i(TAG_CON, String.format("error connecting to device: %s", e.getLocalizedMessage()));
            return false;
        }
    }

    @Override
    public void clearSocketsAndStreams() {
        try {
            super.clearSocketsAndStreams();
        }
        catch (Exception e) {
            Log.i(TAG_CON, "ClearSocketsAndStreams super call threw " + e.getLocalizedMessage());

        }
        // input stream
        try {
            this.iStream.close();
        } catch (Exception e) {
            Log.i(TAG_CON, "ClearSocketsAndStreams iStream.close() (BlueTooth) threw " + e.getLocalizedMessage());
        }
        this.iStream = null;
        //output stream
        try {
            this.oStream.close();
        } catch (Exception e) {
            Log.i(TAG_CON, "ClearSocketsAndStreams oStream.close() (BlueTooth) threw " + e.getLocalizedMessage());
        }
        this.oStream = null;

        //disconnect bluetooth stuff
        try {
            this.btSocket.close();
        } catch (Exception e) {
            Log.i(TAG_CON, "ClearSocketsAndStreams btSocket.close() (BlueTooth) threw " + e.getLocalizedMessage());
        }
        try {
            this.btDevice = null;
        }
        catch (Exception e){
            Log.i(TAG_CON, "ClearSocketsAndStreams this.btDevice = null (BlueTooth) threw " + e.getLocalizedMessage());
        }


    }

    @Override
    public boolean isConnected() {
        boolean connected;
        try {
            if (this.threadHeartBeat == null || !this.threadHeartBeat.isAlive()) return false;

            boolean socketNotNull = this.btSocket != null;
            boolean socketConnected = this.btSocket != null && this.btSocket.isConnected();
            boolean inputStreamNotNull = this.iStream != null;
            boolean outputStreamNotNull = this.oStream != null;
            boolean inputStreamDataAvailable = this.iStream != null && (this.iStream.available() >= 0);
            connected = socketNotNull && socketConnected && inputStreamNotNull && outputStreamNotNull && inputStreamDataAvailable;
            return connected;
        }
        catch (NullPointerException e) {
            Log.i("CON", "Error checking 'is connected'" + e.getLocalizedMessage());
            return false;
        }
        catch (Exception e) {
            Log.i("CON", "Error checking 'is connected'" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Connects to the found device
     *
     * @throws Exception if not found
     */
    public void connect() throws Exception {
        disconnected = false;
        if (isConnecting || isConnected()) return;
        isConnecting = true;
        try {
            getBluetoothAdapter();
            discoverSerialDevice();
        } catch (Exception e) {
            Log.i(TAG_CON, "couldn't connect bluetooth, Exception " + e.getLocalizedMessage());
        }

        try {
            this.btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(CON_BLUETOOTH_SERIAL_PORT_UUID));

            if (!this.btSocket.isConnected()) {
                this.btSocket.connect();
            }
            if (this.btSocket.isConnected()) {
                commandsToSend.clear();
                this.iStream = this.btSocket.getInputStream();
                this.oStream = this.btSocket.getOutputStream();
                threadHeartBeatKill = false;
                startSendThread();
                startReceiveThread();
                startHeartBeatThread();
                isConnecting = false;
                lastRecieved = new Date();
                SERSerial.mHandler.connectionStateChanged(true);
            }
            else {
                isConnecting = false;
                resetConnection();
            }
        } catch (IOException e) {
            isConnecting = false;
            Log.i(TAG_CON, "could not connect IO exception: " + e.getLocalizedMessage());
            this.resetConnection();
            throw (e);
        } catch (Exception e) {
            Log.i(TAG_CON, "could not connect: " + e.getLocalizedMessage());
            isConnecting = false;
            this.resetConnection();
            throw (e);
        }
    }
}
