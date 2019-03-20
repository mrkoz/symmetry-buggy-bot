package ninja.symmetry.robobuggy.serial.Packets;

import java.util.List;

/**
 * Created by koz on 21/07/2014.
 */
public class SERPacketCommandWithData extends SERPacketCommand {
    /**
     * Build from command.
     * @param feature byte module.feature to call e.g. light6
     * @param Data byte data to send or value to set
     */
    public SERPacketCommandWithData(int feature, List<Integer> Data) {
        super();
        this.command = Data.size();
        this.feature = feature;
        this.databuffer = Data;
        generateHash();
    }
}

