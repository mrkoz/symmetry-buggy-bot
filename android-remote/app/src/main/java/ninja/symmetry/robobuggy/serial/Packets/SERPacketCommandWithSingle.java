package ninja.symmetry.robobuggy.serial.Packets;

/**
 * Created by koz on 21/07/2014.
 */
public class SERPacketCommandWithSingle extends SERPacketCommand {
    /**
     * Build from command.
     * @param feature byte module.feature to call e.g. light6
     * @param Data byte data to send or value to set
     */
    public SERPacketCommandWithSingle(int feature, int Data) {
        super();
        this.command = 1;
        this.feature = feature;
        this.databuffer.add(Data);
        generateHash();
    }
}
