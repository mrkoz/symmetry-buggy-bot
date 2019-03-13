package ninja.symmetry.spider.serial.Modules;

import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;

/**
 * Created by koz on 21/06/15.
 */
public class SERModuleId extends SERPacket {
    public static final int FEATURE_ID_SET_IDENTITY       = FEATURE_ID + 0x01;
    public static final int FEATURE_ID_GET_IDENTITY       = FEATURE_ID + 0x02;
    public static final int FEATURE_ID_DISPLAY_IDENTITY   = FEATURE_ID + 0x03;
    public static final int FEATURE_ID_RESET_TO_DEFAULT   = FEATURE_ID + 0x04;

    public static String[] robotNames = {
            "Unset",
            "FrankenBuggy",
            "Steve the spider",
            "Yorick the spider",
            "Bobby the spider",
    };

    public SERModuleId() {
        this.feature = FEATURE_ID;
    }

    public static void messageSetIdentity(int identity) {
        SERSerial.getInstance().sendMessageSingle(FEATURE_ID_SET_IDENTITY, identity);
    }

    public static void messageGetIdentity() {
        SERSerial.getInstance().sendMessage(FEATURE_ID_GET_IDENTITY);
    }

    public static void messageDisplayIdentity() {
        SERSerial.getInstance().sendMessage(FEATURE_ID_DISPLAY_IDENTITY);
    }

    public static void messageResetToDefaultsIdentity() {
        SERSerial.getInstance().sendMessage(FEATURE_ID_RESET_TO_DEFAULT);
    }

    /**
     * android methods
     */
    public static String getNameFromId(int id) {
        return (id > robotNames.length && robotNames.length < id ? "unknown" : robotNames[id]);
    }
}
