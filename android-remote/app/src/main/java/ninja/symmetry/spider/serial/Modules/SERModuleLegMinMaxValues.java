package ninja.symmetry.spider.serial.Modules;

import java.util.List;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.Packets.SERPacketCommand;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 21/06/15.
 */
public class SERModuleLegMinMaxValues extends SERPacketCommand {

    public static final int FEATURE_MIN_MAX                   = 0x20;
    public static final int FEATURE_MIN_MAX_GET_ALL           = FEATURE_MIN_MAX + 0x01;
    public static final int FEATURE_MIN_MAX_SET_ALL           = FEATURE_MIN_MAX + 0x02;
    public static final int FEATURE_MIN_MAX_SET_ALL_AND_MIN   = FEATURE_MIN_MAX + 0x03;
    public static final int FEATURE_MIN_MAX_SET_ALL_AND_MAX   = FEATURE_MIN_MAX + 0x04;
    public static final int FEATURE_MIN_MAX_SET_MIN           = FEATURE_MIN_MAX + 0x05;
    public static final int FEATURE_MIN_MAX_SET_MAX           = FEATURE_MIN_MAX + 0x06;
    public static final int FEATURE_MIN_MAX_SET_ALL_AND_MID   = FEATURE_MIN_MAX + 0x07;
    public static final int FEATURE_MIN_MAX_SET_MID           = FEATURE_MIN_MAX + 0x08;

    public SERModuleLegMinMaxValues() {
        this.feature = FEATURE_MIN_MAX;
    }

    public static void setAllTweakValues(List<Integer> values) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_ALL;
        sp.databuffer.clear();
        for (Integer i : values) {
            sp.databuffer.add(i);
        }
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void setAllTweakValuesAndMin(List<Integer> values) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_ALL_AND_MIN;
        sp.databuffer.clear();
        for (Integer i : values) {
            sp.databuffer.add(i);
        }
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void setAllTweakValuesAndMax(List<Integer> values) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_ALL_AND_MAX;
        sp.databuffer.clear();
        for (Integer i : values) {
            sp.databuffer.add(i);
        }
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void setAllTweakValuesAndMid(List<Integer> values) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_ALL_AND_MID;
        sp.databuffer.clear();
        for (Integer i : values) {
            sp.databuffer.add(i);
        }
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void getAllTweakValues() {
        SERSerial.getInstance().sendMessage(FEATURE_MIN_MAX_GET_ALL);
    }
    public static void setMinValueFor(int servoNumber, int valueMin, int valueMax) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_MIN;
        sp.databuffer.clear();
        sp.databuffer.add(servoNumber & 0xFF);
        sp.databuffer.add(valueMin & 0xFF);
        sp.databuffer.add(valueMax & 0xFF);
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void setMaxValueFor(int servoNumber, int valueMin, int valueMax) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_MAX;
        sp.databuffer.clear();
        sp.databuffer.add(servoNumber & 0xFF);
        sp.databuffer.add(valueMin & 0xFF);
        sp.databuffer.add(valueMax & 0xFF);
        SERSerial.getInstance().sendMessage(sp);
    }

    public static void setMidValueFor(int servoNumber, int valueMin, int valueMax) {
        SERPacket sp = new SERModuleLegMinMaxValues();
        sp.feature = FEATURE_MIN_MAX_SET_MID;
        sp.databuffer.clear();
        sp.databuffer.add(servoNumber & 0xFF);
        sp.databuffer.add(valueMin & 0xFF);
        sp.databuffer.add(valueMax & 0xFF);
        SERSerial.getInstance().sendMessage(sp);
    }
}
