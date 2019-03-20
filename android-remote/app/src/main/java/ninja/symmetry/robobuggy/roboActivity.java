package ninja.symmetry.robobuggy;

import android.app.Activity;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;
import ninja.symmetry.robobuggy.serial.SERSerial;

public abstract class roboActivity extends Activity implements iMessageReceivableActivity{
    public void receiveMessage(SERPacket packet){
    }

    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
    }

}
