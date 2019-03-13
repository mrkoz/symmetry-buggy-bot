package ninja.symmetry.spider.serial.Modules;

import java.util.List;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 21/06/15.
 */
public class SERModuleWalk extends SERPacket{
    public SERModuleWalk() {
        this.feature = FEATURE_WALK;
    }

    /* Items for tweaking  */
    public static final int FEATURE_WALK                        = 0x10;
    public static final int FEATURE_WALK_PROFILE_0              = FEATURE_WALK + 0x01;
    public static final int FEATURE_WALK_PROFILE_1              = FEATURE_WALK + 0x02;
    public static final int FEATURE_WALK_PROFILE_2              = FEATURE_WALK + 0x03;
    public static final int FEATURE_WALK_PROFILE_3              = FEATURE_WALK + 0x04;
    public static final int FEATURE_WALK_STOP                   = FEATURE_WALK + 0x05;
    public static final int FEATURE_WALK_STEP                   = FEATURE_WALK + 0x06;
    public static final int FEATURE_WALK_RESET_LEG_SETTINGS     = FEATURE_WALK + 0x07;
    public static final int FEATURE_WALK_SET_PROFILE_0_RATIOS   = FEATURE_WALK + 0x08;
    public static final int FEATURE_WALK_SET_PROFILE_1_RATIOS   = FEATURE_WALK + 0x09;
    public static final int FEATURE_WALK_SET_PROFILE_2_RATIOS   = FEATURE_WALK + 0x0a;
    public static final int FEATURE_WALK_SET_PROFILE_3_RATIOS   = FEATURE_WALK + 0x0b;
    public static final int FEATURE_WALK_GET_PROFILE_0_RATIOS   = FEATURE_WALK + 0x0c;
    public static final int FEATURE_WALK_GET_PROFILE_1_RATIOS   = FEATURE_WALK + 0x0d;
    public static final int FEATURE_WALK_GET_PROFILE_2_RATIOS   = FEATURE_WALK + 0x0e;
    public static final int FEATURE_WALK_GET_PROFILE_3_RATIOS   = FEATURE_WALK + 0x0f;

    public static void walkForward() {
        walkForward(50);
    }
    public static void walkForward(int speed) {
        SERModuleWalk.walk(FEATURE_WALK_PROFILE_0, speed);
    }

    public static void walkReverse() {
        walkReverse(50);
    }
    public static void walkReverse(int speed) {
        SERModuleWalk.walk(FEATURE_WALK_PROFILE_1, speed);
    }

    public static void walkClockwise() {
        walkClockwise(50);
    }
    public static void walkClockwise(int speed) {
        SERModuleWalk.walk(FEATURE_WALK_PROFILE_2, speed);
    }

    public static void walkAntiClockwise() {
        walkAntiClockwise(100);
    }
    public static void walkAntiClockwise(int speed) {
        walk(FEATURE_WALK_PROFILE_3, speed);
    }

    public static void walk(int profile, int speedRatio) {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = profile;
        packet.databuffer.add(speedRatio);
        SERSerial.getInstance().sendMessage(packet);
    }

    public static void getWalkProfile(int profile) {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = FEATURE_WALK_GET_PROFILE_0_RATIOS + profile;
        SERSerial.getInstance().sendMessage(packet);
    }

    public static void setWalkProfile(int profile, List<Integer> values) {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = FEATURE_WALK_SET_PROFILE_0_RATIOS + profile;
        for (Integer i : values) {
            packet.databuffer.add(i);
        }
        SERSerial.getInstance().sendMessage(packet);

    }

    public static void walkStop() {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = FEATURE_WALK_STOP;
        SERSerial.getInstance().sendMessage(packet);
    }

    public static void walkStep() {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = FEATURE_WALK_STEP;
        SERSerial.getInstance().sendMessage(packet);
    }

    public static void resetLegSettings() {
        SERModuleWalk packet = new SERModuleWalk();
        packet.feature = FEATURE_WALK_RESET_LEG_SETTINGS;
        SERSerial.getInstance().sendMessage(packet);
    }




}
