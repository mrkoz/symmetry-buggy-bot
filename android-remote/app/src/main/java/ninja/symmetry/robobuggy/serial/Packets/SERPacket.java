package ninja.symmetry.robobuggy.serial.Packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import android.util.Log;

import ninja.symmetry.robobuggy.serial.SERDevice;


/**
 * Created by koz on 14/07/2014.
 */

public class SERPacket {
    /* Section: Constants */
    /* some static constants variables for types */
    /** basic status command types **/
    public static final int COMMAND = 0x01;
    public static final int HELO = 0xFB;
    public static final int ACK = 0xFC;
    public static final int NACK = 0xFD;
    public static final int FAIL = 0xFE;


    public static final int STATUS_DEBUG_ON = 0xF0;
    public static final int STATUS_DEBUG_OFF = 0xF1;
    public static final int STATUS_POWER_UP = 0xF2;
    public static final int STATUS_POWER_DOWN = 0xF3;
    public static final int STATUS_RESET_CPU = 0xF4;
    public static final int STATUS_RESET_COMMS = 0xF5;
    public static final int STATUS_ERASE_EEPROM = 0xF6;
    public static final int STATUS_RESET_TO_DEFAULTS = 0xF7;
    public static final int STATUS_RESET_ZERO_ONE = 0xF8;
    public static final int STATUS_RESET_ZERO_TWO = 0xF9;
    public static final int STATUS_AUX = 0xFA;


    /* Features in the controller */
    public static final int FEATURE_DRIVE  = 0x10;
    public static final int FEATURE_DRIVE_FORWARD        = FEATURE_DRIVE + 0x01;
    public static final int FEATURE_DRIVE_REVERSE        = FEATURE_DRIVE + 0x02;
    public static final int FEATURE_DRIVE_ANTICLOCKWISE  = FEATURE_DRIVE + 0x03;
    public static final int FEATURE_DRIVE_CLOCKWISE      = FEATURE_DRIVE + 0x04;

    public static final int FEATURE_STOP  = 0x20;


    public static final int FEATURE_EXEC   = 0x30;
    public static final int FEATURE_EXEC_1 = FEATURE_EXEC + 0x01;
    public static final int FEATURE_EXEC_2 = FEATURE_EXEC + 0x02;
    public static final int FEATURE_EXEC_3 = FEATURE_EXEC + 0x03;
    public static final int FEATURE_EXEC_4 = FEATURE_EXEC + 0x04;
    public static final int FEATURE_EXEC_5 = FEATURE_EXEC + 0x05;
    public static final int FEATURE_EXEC_6 = FEATURE_EXEC + 0x06;
    public static final int FEATURE_EXEC_7 = FEATURE_EXEC + 0x07;
    public static final int FEATURE_EXEC_8 = FEATURE_EXEC + 0x08;

    /** function type **/

    /* parameter location - for the byte array */
    public static final int P_START = 0;
    public static final int P_MESSAGESIZE = 1;
    public static final int P_FEATURE = 2;
    public static final int P_CHECKSUM = 3;

    /* Start bit */
    public static final byte START = (byte) 0xFF;

    /* Pre-defined data lengths */
    public static final int COMMAND_LENGTH = 4;

    /* Section: members */
    public int command;
    public int feature;
    public int checkSum;
    public List<Integer> databuffer = new ArrayList<Integer>();

    public int packetType = COMMAND;

    public int read_counter = 0;

    /* Add data helpers for sendpacket */
    public void add_data(int value) {
        if (value > 0xFFFF) {
            Log.i(SERDevice.TAG_CON, "Error adding value, value is > 0xFFFF");
            return;
        }
        if (value > 0xFF) {
            int msb = (value / 256);
            int lsb = (value % 0xFF);
            databuffer.add(msb);
            databuffer.add(lsb);
        }
        else {
            int lsb = (value % 0xFF);
            databuffer.add(lsb);
        }
    }
    /**
     * Generates the packet's "super-feature"
     */
    public int featureSuper() {
        return (this.feature - (this.feature % 0x10));
    }

    public void reset_data_counter() {
        read_counter = 0;
    }

    public int get_next_word() {
        return databuffer.get(read_counter++) * 256 + databuffer.get(read_counter++);
    }

    public int get_next_value() {
        return databuffer.get(read_counter++);
    }

    /* Section: members for helper calls */
    /**
     * Empty initializer.
     */
    public SERPacket() {
        super();
    }

    /**
     * built for status commands
     * @param type type of packet
     */
    public SERPacket(int type) {
        this.packetType = type;
    }

    public Boolean isStatusCommand() {
        return this.packetType > 0xF0;
    }

    /**
     * Build from command.
     * @param Command SERPacket.COMMAND_* (byte) command to perform
     * @param feature byte module.feature to call e.g. light6
     */
    public SERPacket(int Command, int feature) {
        super();
        this.command = Command;
        this.feature = feature;
        generateHash();
    }

    /**
     * Generates a packet from an input queue
     * @param inputQueue the Queue for decoding
     * @return SERPacket or null
     */
    public static SERPacket fromInputQueue(Queue<Byte> inputQueue) {
        // find the start character
        while (inputQueue.peek() != null && inputQueue.peek() != START) {
            inputQueue.remove();
        }

        // do we have a command or at least a command length
        if (inputQueue.size() > 1 && inputQueue.size() > (Byte)inputQueue.toArray()[1] + COMMAND_LENGTH - 1) {
            //dump the start character
            inputQueue.remove();

            int firstByte = 0xff & inputQueue.remove();

            if (firstByte > 0xF0) {
                return new SERPacket(firstByte);
            }
            else {
                SERPacket packet = new SERPacket();
                packet.packetType = COMMAND;
                packet.command = 0xff & firstByte;
                packet.feature = 0xff & inputQueue.remove();
                packet.checkSum = 0xff & inputQueue.remove();
                while (inputQueue.size() > 0) {
                    packet.databuffer.add(0xff & inputQueue.remove());
                }

                return packet;
            }

        }
        if (inputQueue.peek() != null && inputQueue.peek() > 0xF0) {
            return new SERPacket(0xff & inputQueue.remove());
        }

        return null;
    }

    /**
     * Builds a full command from the current instance
     * @return full command byte[]
     */
    public byte[] buildCommand() {
        byte[] output = new byte[2];

        switch (this.packetType) {
            case COMMAND:
                this.generateHash();
                output = new byte[COMMAND_LENGTH + this.databuffer.size() + 1];
                output[P_START] = START;
                output[P_MESSAGESIZE] = (byte) this.databuffer.size();
                output[P_FEATURE] = (byte) this.feature;
                output[P_CHECKSUM] = (byte) this.checkSum;
                for (int i = 0; i < this.databuffer.size(); i++) {
                    output[COMMAND_LENGTH + i] = this.databuffer.get(i).byteValue();
                }
                break;
            default:
                output[P_START] = START;
                output[P_MESSAGESIZE] = (byte) this.packetType;
                break;
        }

        return output;

    }

    /**
     * Generates a hash based on the current instance.
     */
    public void generateHash() {
        this.checkSum = generateHash((byte) this.feature, this.databuffer);
    }

    /**
     * Generates hash command based on the specified command parts as bytes
     * @param feature to control
     * @param data to value
     * @return byte the hash
     */
    public byte generateHash(byte feature, List<Integer> data) {
        int total = 0;
        total += data.size() & 0xFF;
        total += (int) feature & 0xFF;
        for (int i : data) {
            total += i;
        }

        int difference = 256 - (total % 256) ;

        return (byte) difference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.packetType == COMMAND) {
            sb.append(" feature: ");
            sb.append(feature);
            sb.append(" Hash: ");
            sb.append(checkSum & 0xff);
            sb.append(" size: ");
            sb.append(command & 0xff);
            sb.append(" data: ");
            for (int value : this.databuffer) {
                sb.append(String.valueOf(value));
                sb.append(" ");
            }
        }
        else {
            sb.append(statusNameFromID(this.packetType));
        }
        return sb.toString();

    }

    public String statusNameFromID(int statusId) {
        switch (statusId) {
            case HELO:
                return "HELO";
            case ACK:
                return "ACK";
            case NACK:
                return "NACK";
            case FAIL:
                return "FAIL";
            case STATUS_DEBUG_ON:
                return "STATUS_DEBUG_ON";
            case STATUS_DEBUG_OFF:
                return "STATUS_DEBUG_OFF";
            case STATUS_POWER_UP:
                return "STATUS_POWER_UP";
            case STATUS_POWER_DOWN:
                return "STATUS_POWER_DOWN";
            case STATUS_RESET_CPU:
                return "STATUS_RESET_CPU";
            case STATUS_RESET_COMMS:
                return "STATUS_RESET_COMMS";
            case STATUS_ERASE_EEPROM:
                return "STATUS_ERASE_EEPROM";
            case STATUS_RESET_TO_DEFAULTS:
                return "STATUS_RESET_TO_DEFAULTS";
            case STATUS_RESET_ZERO_ONE:
                return "STATUS_RESET_ZERO_ONE";
            case STATUS_RESET_ZERO_TWO:
                return "STATUS_RESET_ZERO_TWO";
            case STATUS_AUX:
                return "STATUS_AUX";
            default:
                return "COMMAND";
        }
    }

    public static SERPacket status_HELO() {
        return new SERPacket(HELO);
    }

    public static SERPacket status_ACK(){
        return new SERPacket(ACK);
    }
    public static SERPacket status_NACK(){
        return new SERPacket(NACK);
    }
    public static SERPacket status_FAIL(){
        return new SERPacket(FAIL);
    }


    public static SERPacket status_STATUS_DEBUG_ON(){
        return new SERPacket(STATUS_DEBUG_ON);
    }
    public static SERPacket status_STATUS_DEBUG_OFF(){
        return new SERPacket(STATUS_DEBUG_OFF);
    }
    public static SERPacket status_STATUS_POWER_UP(){
        return new SERPacket(STATUS_POWER_UP);
    }
    public static SERPacket status_STATUS_POWER_DOWN(){
        return new SERPacket(STATUS_POWER_DOWN);
    }
    public static SERPacket status_STATUS_RESET_CPU(){
        return new SERPacket(STATUS_RESET_CPU);
    }
    public static SERPacket status_STATUS_RESET_COMMS(){
        return new SERPacket(STATUS_RESET_COMMS);
    }
    public static SERPacket status_STATUS_ERASE_EEPROM(){
        return new SERPacket(STATUS_ERASE_EEPROM);
    }
    public static SERPacket status_STATUS_RESET_TO_DEFAULTS(){
        return new SERPacket(STATUS_RESET_TO_DEFAULTS);
    }
    public static SERPacket status_STATUS_RESET_ZERO_ONE(){
        return new SERPacket(STATUS_RESET_ZERO_ONE);
    }
    public static SERPacket status_STATUS_RESET_ZERO_TWO(){
        return new SERPacket(STATUS_RESET_ZERO_TWO);
    }
    public static SERPacket status_STATUS_AUX(){
        return new SERPacket(STATUS_AUX);
    }
}
