package ninja.symmetry.robobuggy.serial;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import ninja.symmetry.robobuggy.MainActivity;
import ninja.symmetry.robobuggy.iMessageReceivableActivity;
import ninja.symmetry.robobuggy.serial.Packets.SERPacket;

/**
 * Created by koz on 20/10/2014.
 */
public class SERReceiveHandler extends Handler {

    private final MainActivity mActivity;
    private final SERSerial mSerialController;
    private ArrayList<iMessageReceivableActivity> activities = new ArrayList<iMessageReceivableActivity>();

    public void addActivity(iMessageReceivableActivity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public void removeActivity(iMessageReceivableActivity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
    }

    public SERReceiveHandler(MainActivity activity, SERSerial serialController) {
        mActivity = activity;
        mSerialController = serialController;
        SERSerial.mHandler = this;
    }

    @Override
    public void handleMessage(Message msg) {
        mActivity.receiveMessage((SERPacket) msg.obj);

        for (iMessageReceivableActivity activity : activities) {
            activity.receiveMessage((SERPacket) msg.obj);
        }
    }

    public void connectionStateChanged(boolean connected) {
        final boolean connectedF = connected;
        for (final iMessageReceivableActivity activity : activities) {
            ((Activity) activity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.receiveConnectionStateChange(connectedF);
                }
            });
        }
    }
}
