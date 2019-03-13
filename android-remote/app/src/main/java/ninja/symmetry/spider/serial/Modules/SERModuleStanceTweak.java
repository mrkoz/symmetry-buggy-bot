package ninja.symmetry.spider.serial.Modules;

import java.util.List;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 21/06/15.
 */
public class SERModuleStanceTweak extends SERPacket {
    public static final String[] stances = {"clasp","stand","lift1","lift2","wave","wave2","pose1","pose2","straight1A","straight1B","straight2A","straight2B","rotate1A","rotate1B","rotate2A","rotate2B",};

    public static final int STANCES_COUNT = 16;

    public static final int FEATURE_STANCE                  = 0x30;
    public static final int FEATURE_STANCE_GET_TWEAK        = FEATURE_STANCE + 0x01;
    public static final int FEATURE_STANCE_SET_TWEAK        = FEATURE_STANCE + 0x02;
    public static final int FEATURE_STANCE_GET_TWEAK_ALL    = FEATURE_STANCE + 0x03;

    public SERModuleStanceTweak() {
        this.feature = FEATURE_STANCE;
    }

    public static void getStanceTweak(int stance) {
        SERSerial.getInstance().sendMessageSingle(FEATURE_STANCE_GET_TWEAK, stance);
    }

    public static void setStanceTweak(int stance, List<Integer> values, boolean reflect) {
        SERPacket packet = new SERModuleStanceTweak();
        packet.databuffer.add(stance);
        packet.databuffer.add(reflect?1:0);
        for (int value : values) {
            packet.databuffer.add(value);
        }
        packet.feature = FEATURE_STANCE_SET_TWEAK;

        SERSerial.getInstance().sendMessage(packet);
    }

    public static void getAllStanceTweaks() {
        SERSerial.getInstance().sendMessage(FEATURE_STANCE_GET_TWEAK_ALL);
    }
}
