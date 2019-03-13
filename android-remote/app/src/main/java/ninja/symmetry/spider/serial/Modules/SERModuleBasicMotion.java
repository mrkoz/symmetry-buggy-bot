package ninja.symmetry.spider.serial.Modules;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.Packets.SERPacketCommandWithSingle;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 2/07/15.
 */
public class SERModuleBasicMotion extends SERPacket {
    public static final int FEATURE_BASIC_MOTION_MOVE_TO_STANCE =           FEATURE_BASIC_MOTION + 0x01;
    public static final int FEATURE_BASIC_MOTION_MOVE_ODD_TO_STANCE =       FEATURE_BASIC_MOTION + 0x02;
    public static final int FEATURE_BASIC_MOTION_MOVE_EVEN_TO_STANCE =      FEATURE_BASIC_MOTION + 0x03;
    public static final int FEATURE_BASIC_MOTION_CHANGE_TO_STANCE =         FEATURE_BASIC_MOTION + 0x04;
    public static final int FEATURE_BASIC_MOTION_CHANGE_ODD_TO_STANCE =     FEATURE_BASIC_MOTION + 0x05;
    public static final int FEATURE_BASIC_MOTION_CHANGE_EVEN_TO_STANCE =    FEATURE_BASIC_MOTION + 0x06;

    public static final int SERVO_NUM = 0;
    public static final int SERVO_MIN = 1;
    public static final int SERVO_MAX = 2;
    public static final int SERVO_STANCE_CLASP            = 3;
    public static final int SERVO_STANCE_STAND            = 4;
    public static final int SERVO_STANCE_LIFT_1           = 5;
    public static final int SERVO_STANCE_LIFT_2           = 6;
    public static final int SERVO_STANCE_WAVE             = 7;
    public static final int SERVO_STANCE_WAVE_2           = 8;
    public static final int SERVO_STANCE_POSE_1           = 9;
    public static final int SERVO_STANCE_POSE_2           = 10;
    public static final int SERVO_STANCE_STRAIGHT_1_A     = 11;
    public static final int SERVO_STANCE_STRAIGHT_1_B     = 12;
    public static final int SERVO_STANCE_STRAIGHT_2_A     = 13;
    public static final int SERVO_STANCE_STRAIGHT_2_B     = 14;
    public static final int SERVO_STANCE_ROTATE_1_A       = 15;
    public static final int SERVO_STANCE_ROTATE_1_B       = 16;
    public static final int SERVO_STANCE_ROTATE_2_A       = 17;
    public static final int SERVO_STANCE_ROTATE_2_B       = 18;

    public SERModuleBasicMotion() {
        this.feature = FEATURE_BASIC_MOTION;
    }

    public static void setAllToStance(int stance) {
        SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(FEATURE_BASIC_MOTION_MOVE_TO_STANCE, stance));
    }

    public static void setOddToStance(int stance) {
        SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(FEATURE_BASIC_MOTION_MOVE_ODD_TO_STANCE, stance));
    }

    public static void setEvenToStance(int stance) {
        SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(FEATURE_BASIC_MOTION_MOVE_EVEN_TO_STANCE, stance));
    }

    public static void changeAllToStance(int stance) {
        SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(FEATURE_BASIC_MOTION_CHANGE_TO_STANCE, stance));
    }

}
