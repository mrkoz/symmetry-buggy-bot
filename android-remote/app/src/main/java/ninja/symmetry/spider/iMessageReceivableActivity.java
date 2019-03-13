package ninja.symmetry.spider;

import android.app.Activity;

import ninja.symmetry.spider.serial.Packets.SERPacket;

/**
 * Created by koz on 14/05/15.
 */
public interface iMessageReceivableActivity {
    void receiveMessage(SERPacket packet);
    void receiveConnectionStateChange(boolean connected);
}
