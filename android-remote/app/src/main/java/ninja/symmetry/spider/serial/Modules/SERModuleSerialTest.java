package ninja.symmetry.spider.serial.Modules;

import java.util.ArrayList;
import java.util.List;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 22/06/15.
 */
public class SERModuleSerialTest extends SERPacket {

    public static final int FEATURE_TEST_SEND_COMMAND                   = FEATURE_TEST + 0x01;
    public static final int FEATURE_TEST_SEND_COMMAND_SINGLE_DATA       = FEATURE_TEST + 0x02;
    public static final int FEATURE_TEST_SEND_COMMAND_8_DATA            = FEATURE_TEST + 0x03;
    public static final int FEATURE_TEST_SEND_COMMAND_60_DATA           = FEATURE_TEST + 0x04;
    public static final int FEATURE_TEST_SET_SERVO_VALUE                = FEATURE_TEST + 0x05;
    public static final int FEATURE_TEST_MOVE_ALL_TO_MIN                = FEATURE_TEST + 0x06;
    public static final int FEATURE_TEST_MOVE_ALL_TO_MAX                = FEATURE_TEST + 0x08;
    public static final int FEATURE_TEST_MOVE_JOINT_TO_MIN              = FEATURE_TEST + 0x09;
    public static final int FEATURE_TEST_MOVE_JOINT_TO_MAX              = FEATURE_TEST + 0x0B;
    public SERModuleSerialTest() {
        this.feature = FEATURE_TEST;
    }

    public static void messageSendCommand() {
        SERSerial.getInstance().sendMessage(FEATURE_TEST_SEND_COMMAND);
    }

    public static void messageSendCommandSingle() {
        SERSerial.getInstance().sendMessageSingle(SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_SINGLE_DATA, 11);
    }
    public static void messageSendCommandEight() {
        List<Integer> toSend = new ArrayList<Integer>();
        toSend.add(11);
        toSend.add(12);
        toSend.add(13);
        toSend.add(14);
        toSend.add(15);
        toSend.add(16);
        toSend.add(17);
        toSend.add(18);

        SERSerial.getInstance().sendMessageWithData(SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_8_DATA, toSend);
    }
    public static void messageSendCommand60() {
        List<Integer> toSend = new ArrayList<Integer>();
        for (int i = 0; i < 60; i++) {
            toSend.add(i*2);
        }
        SERSerial.getInstance().sendMessageWithData(SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_60_DATA, toSend);
    }

    public static void setServoToValue(int servo, int value) {
        SERPacket packet = new SERPacket();
        packet.add_data(servo);
        packet.add_data(value/256);
        packet.add_data(value%256);
        packet.feature = SERModuleSerialTest.FEATURE_TEST_SET_SERVO_VALUE;
        SERSerial.getInstance().sendMessage(packet);
    }

    public static void moveAllToMin() {
        SERSerial.getInstance().sendMessage(FEATURE_TEST_MOVE_ALL_TO_MIN);
    }

    public static void moveAllToMax() {
        SERSerial.getInstance().sendMessage(FEATURE_TEST_MOVE_ALL_TO_MAX);
    }

    public static void moveBaseToMin() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MIN, 0);
    }

    public static void moveBaseToMax() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MAX, 0);
    }

    public static void moveShoulderToMin() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MIN, 1);
    }

    public static void moveShoulderToMax() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MAX, 1);
    }

    public static void moveElbowToMin() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MIN, 2);
    }

    public static void moveElbowToMax() {
        SERSerial.getInstance().sendMessageSingle(FEATURE_TEST_MOVE_JOINT_TO_MAX, 2);
    }




}
