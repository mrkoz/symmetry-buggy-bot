package ninja.symmetry.robobuggy;

import android.os.Bundle;
import android.view.View;
import com.erz.joysticklibrary.JoyStick;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;
import ninja.symmetry.robobuggy.serial.Packets.SERPacketCommand;
import ninja.symmetry.robobuggy.serial.Packets.SERPacketCommandWithSingle;
import ninja.symmetry.robobuggy.serial.SERSerial;
import java.time.Duration;
import java.time.Instant;

/* created from https://android-arsenal.com/details/1/2712 */

public class DrivingJoystick extends roboActivity implements JoyStick.JoyStickListener {
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

    public Instant lastMsg = Instant.now();
    public int millisLimit = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_joystick);
        joyStick = findViewById(R.id.joyStick);
        joyStick.setListener(this);
        joyStick.setType(JoyStick.TYPE_8_AXIS);
    }

    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.JoyStickLayout).setBackgroundColor(getResources().getColor(R.color.disconnected, getTheme()));
        else
            findViewById(R.id.JoyStickLayout).setBackgroundColor(getResources().getColor(R.color.connected, getTheme()));
    }

    public void onMove(JoyStick joyStick, double angle, double power, int direction) {
        Duration timeElapsed = Duration.between(lastMsg, Instant.now());

        if (direction != DIRECTION_CENTER || timeElapsed.toMillis() > millisLimit) {
            switch (direction) {
                case DIRECTION_CENTER:
                    SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_STOP));
                    break;
                case DIRECTION_UP:
                case DIRECTION_LEFT_UP:
                case DIRECTION_UP_RIGHT:
                    SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(SERPacket.FEATURE_DRIVE_FORWARD, (int) power));
                    break;
                case DIRECTION_LEFT:
                    SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(SERPacket.FEATURE_DRIVE_ANTICLOCKWISE, (int) power));
                    break;
                case DIRECTION_RIGHT:
                    SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(SERPacket.FEATURE_DRIVE_CLOCKWISE, (int) power));
                    break;
                case DIRECTION_DOWN:
                case DIRECTION_RIGHT_DOWN:
                case DIRECTION_DOWN_LEFT:
                    SERSerial.getInstance().sendMessage(new SERPacketCommandWithSingle(SERPacket.FEATURE_DRIVE_REVERSE, (int) power));
                    break;
            }
            lastMsg = Instant.now();
        }

    }

    public void onTap() {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_STOP));
    }

    public void onDoubleTap() {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_STOP));
    }

    public void buttonPush1(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_1));
    }

    public void buttonPush2(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_2));
    }

    public void buttonPush3(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_3));
    }

    public void buttonPush4(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_4));
    }

    public void buttonPush5(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_5));
    }

    public void buttonPush6(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_6));
    }

    public void buttonPush7(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_7));
    }

    public void buttonPush8(View view) {
        SERSerial.getInstance().sendMessage(new SERPacketCommand(SERPacket.FEATURE_EXEC_8));
    }


}
