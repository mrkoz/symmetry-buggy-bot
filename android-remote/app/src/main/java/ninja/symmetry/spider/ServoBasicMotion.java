package ninja.symmetry.spider;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ninja.symmetry.spider.serial.Modules.SERModuleBasicMotion;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;


public class ServoBasicMotion extends Activity implements iMessageReceivableActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servo_basic_motion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_servo_basic_motion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
    }
    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.basicMotionLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.basicMotionLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    public void receiveMessage(SERPacket packet) {
    }

    /* move odd/even to */


    public void setAllToMin(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_MIN);
    }
    public void setAllToMax(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_MAX);
    }
    public void setAllToClasp(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);
    }
    public void setAllToStand(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);
    }
    public void setAllToLift1(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_1);
    }
    public void setAllToLift2(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_2);
    }
    public void setAllToWave(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE);
    }
    public void setAllToWave2(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE_2);
    }
    public void setAllToPOSE_1(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_1);
    }
    public void setAllToPOSE_2(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_2);
    }
    public void setAllToSTRAIGHT_1_A(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_A);
    }
    public void setAllToSTRAIGHT_1_B(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_B);
    }
    public void setAllToSTRAIGHT_2_A(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_A);
    }
    public void setAllToSTRAIGHT_2_B(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_B);
    }
    public void setAllToROTATE_1_A(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_A);
    }
    public void setAllToROTATE_1_B(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_B);
    }
    public void setAllToROTATE_2_A(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_A);
    }
    public void setAllToROTATE_2_B(View view) {
        SERModuleBasicMotion.setAllToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_B);
    }


    public void setEvenToMin(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_MIN);
    }
    public void setEvenToMax(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_MAX);
    }
    public void setEvenToClasp(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);
    }
    public void setEvenToStand(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);
    }
    public void setEvenToLift1(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_1);
    }
    public void setEvenToLift2(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_2);
    }
    public void setEvenToWave(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE);
    }
    public void setEvenToWave2(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE_2);
    }
    public void setEvenToPOSE_1(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_1);
    }
    public void setEvenToPOSE_2(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_2);
    }
    public void setEvenToSTRAIGHT_1_A(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_A);
    }
    public void setEvenToSTRAIGHT_1_B(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_B);
    }
    public void setEvenToSTRAIGHT_2_A(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_A);
    }
    public void setEvenToSTRAIGHT_2_B(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_B);
    }
    public void setEvenToROTATE_1_A(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_A);
    }
    public void setEvenToROTATE_1_B(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_B);
    }
    public void setEvenToROTATE_2_A(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_A);
    }
    public void setEvenToROTATE_2_B(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_B);
    }


    public void setOddToMin(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_MIN);
    }
    public void setOddToMax(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_MAX);
    }
    public void setOddToClasp(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_CLASP);
    }
    public void setOddToStand(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_STAND);
    }
    public void setOddToLift1(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_1);
    }
    public void setOddToLift2(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_LIFT_2);
    }
    public void setOddToWave(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE);
    }
    public void setOddToWave2(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_WAVE_2);
    }
    public void setOddToPOSE_1(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_1);
    }
    public void setOddToPOSE_2(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_POSE_2);
    }
    public void setOddToSTRAIGHT_1_A(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_A);
    }
    public void setOddToSTRAIGHT_1_B(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_1_B);
    }
    public void setOddToSTRAIGHT_2_A(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_A);
    }
    public void setOddToSTRAIGHT_2_B(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_STRAIGHT_2_B);
    }
    public void setOddToROTATE_1_A(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_A);
    }
    public void setOddToROTATE_1_B(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_1_B);
    }
    public void setOddToROTATE_2_A(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_A);
    }
    public void setOddToROTATE_2_B(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_STANCE_ROTATE_2_B);
    }
}
