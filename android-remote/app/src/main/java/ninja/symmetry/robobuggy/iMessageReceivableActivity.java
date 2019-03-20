package ninja.symmetry.robobuggy;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;

/**
 * Created by koz on 14/05/15.
 */
public interface iMessageReceivableActivity {
    void receiveMessage(SERPacket packet);
    void receiveConnectionStateChange(boolean connected);
}
