package ninja.symmetry.spider;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ninja.symmetry.spider.serial.Modules.SERModuleBasicMotion;
import ninja.symmetry.spider.serial.Modules.SERModuleWalk;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERReceiveHandler;
import ninja.symmetry.spider.serial.SERSerial;
import ninja.symmetry.spider.R;

public class MainActivity extends Activity implements iMessageReceivableActivity {
    private SERReceiveHandler rHandler = new SERReceiveHandler(this, SERSerial.getInstance());

    public Intent intentTweakLeg;
    public Intent intentTweakStance;
    public Intent intentIdentity;
    public Intent intentTests;
    public Intent intentServoBasicMotion;
    public Intent intentJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentTweakLeg = new Intent(this, setMinMaxValuesDialog.class);
        intentTweakStance = new Intent(this, stanceTweakActivity.class);
        intentIdentity = new Intent(this, robotIdActivity.class);
        intentTests = new Intent(this, serialTestActivity.class);
        intentServoBasicMotion = new Intent(this, ServoBasicMotion.class);
        intentJoystick = new Intent(this, walking_joystick.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar feature clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return (id == R.id.action_settings || super.onOptionsItemSelected(item));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.txtDeviceName)).setText(SERSerial.get_device_name());
        SERSerial.getInstance().addActivityToHandler(this);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
    }

    public void receiveMessage(SERPacket packet){
    }

    public void setDeviceToSteph(View view) {
        ((TextView) findViewById(R.id.txtDeviceName)).setText("HC-05");
        SERSerial.set_device_name("HC-05");
    }

    public void setDeviceToYorick(View view) {
        ((TextView) findViewById(R.id.txtDeviceName)).setText("YorickDuino");
        SERSerial.set_device_name("YorickDuino");
    }


    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.MainActivityLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.MainActivityLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    public void setDeviceToSteve(View view) {
        ((TextView) findViewById(R.id.txtDeviceName)).setText("SteveDuino");
        SERSerial.set_device_name("SteveDuino");
    }

    /* Walking methods */
    public void stop(View view) {
        SERModuleWalk.walkStop();
    }

    /* various debug things */
    public void messageSendStatusDebugOn(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_ON));
    }
    public void messageSendStatusDebugOff(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_OFF));
    }

    public void DebugResetLegSettings(View view) {
        SERModuleWalk.resetLegSettings();
    }
    public void walkDoStep(View view) {
        SERModuleWalk.walkStep();
    }

    public void popupLegTweaks(View view) {
        startActivity(intentTweakLeg);
    }
    public void popupStanceTweaks(View view) {
        startActivity(intentTweakStance);
    }
    public void popupIdentity(View view) {
        startActivity(intentIdentity);
    }
    public void popupTests(View view) {
        startActivity(intentTests);
    }

    public void popupJoystick(View view) {
        startActivity(intentJoystick);
    }


    public void popupBasicMotion(View view) {
        startActivity(intentServoBasicMotion);
    }

    public void moveAllToPose1(View view) {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_1);
    }
    public void moveAllToPose2(View view) {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_2);
    }
    public void moveAllToClasp(View view) {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);
    }
    public void moveAllToStand(View view) {
        SERModuleBasicMotion.changeAllToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);
    }


    public void setAllToStand(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);
    }
    public void setAllToClasp(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);
    }
    public void setAllToWave(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE);
    }
    public void setAllToWave2(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE_2);
    }
    public void setAllToPose(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_1);
    }
    public void setAllToPose2(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_2);
    }
}
