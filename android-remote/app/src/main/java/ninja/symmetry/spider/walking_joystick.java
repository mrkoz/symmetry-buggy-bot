package ninja.symmetry.spider;

import android.app.Activity;
import android.os.Bundle;

import com.erz.joysticklibrary.JoyStick;

import ninja.symmetry.spider.serial.Modules.SERModuleBasicMotion;
import ninja.symmetry.spider.serial.Modules.SERModuleWalk;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;

/* created from https://android-arsenal.com/details/1/2712 */

public class walking_joystick extends Activity implements JoyStick.JoyStickListener, iMessageReceivableActivity {
    JoyStick joyStick;
    public static final int DIRECTION_CENTER = -1;
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_LEFT_UP = 1;
    public static final int DIRECTION_UP = 2;
    public static final int DIRECTION_UP_RIGHT = 3;
    public static final int DIRECTION_RIGHT = 4;
    public static final int DIRECTION_RIGHT_DOWN = 5;
    public static final int DIRECTION_DOWN = 6;
    public static final int DIRECTION_DOWN_LEFT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_joystick);
        joyStick = (JoyStick) findViewById(R.id.joyStick);
        joyStick.setListener(this);
        joyStick.setType(JoyStick.TYPE_4_AXIS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
    }
    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.JoyStickLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.JoyStickLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    public void receiveMessage(SERPacket packet) {
    }

    public void onMove(JoyStick joyStick, double angle, double power, int direction) {
        switch (direction) {
            case DIRECTION_CENTER:
                SERModuleWalk.walkStop();
                break;
            case DIRECTION_UP:
            case DIRECTION_LEFT_UP:
            case DIRECTION_UP_RIGHT:
                SERModuleWalk.walkForward((int) power);
                break;
            case DIRECTION_LEFT:
                SERModuleWalk.walkAntiClockwise((int) power);
                break;
            case DIRECTION_RIGHT:
                SERModuleWalk.walkClockwise((int) power);
                break;
            case DIRECTION_DOWN:
            case DIRECTION_RIGHT_DOWN:
            case DIRECTION_DOWN_LEFT:
                SERModuleWalk.walkReverse((int) power);
                break;
        }

    }

    public void onTap() {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);

    }

    public void onDoubleTap() {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);

    }


}
