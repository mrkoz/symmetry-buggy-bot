package ninja.symmetry.robobuggy.serial.Packets;

/**
 * Created by koz on 14/07/2014.
 */

/**
 * Standard command packet.
 */
public class SERPacketCommand extends SERPacket {
    public SERPacketCommand() {
        this.packetType = COMMAND;
    }

    public SERPacketCommand(int feature) {
        super();
        this.packetType = COMMAND;
        this.feature = feature;
        generateHash();
    }
}
