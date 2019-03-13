package ninja.symmetry.spider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ninja.symmetry.spider.serial.Modules.SERModuleSerialTest;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;
import ninja.symmetry.spider.R;


public class serialTestActivity extends Activity implements iMessageReceivableActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
    }

    @Override protected void onDestroy () {
        SERSerial.getInstance().removeActivityToHandler(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serial_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar feature clicks here. The action bar will
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
    public void receiveMessage(SERPacket packet) {
        if (packet.featureSuper() == SERPacket.FEATURE_TEST) {
            switch (packet.feature) {
                case SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND:
                case SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_SINGLE_DATA:
                case SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_8_DATA:
                case SERModuleSerialTest.FEATURE_TEST_SEND_COMMAND_60_DATA:
                    Log.i("TEST", packet.toString());
                    break;
            }
        }
    }
    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.testingLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.testingLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    /**
     * android methods
     */

    public void messageSendCommand(View view) {
        SERModuleSerialTest.messageSendCommand();
    }
    public void messageSendCommandSingle(View view) {
        SERModuleSerialTest.messageSendCommandSingle();
    }
    public void messageSendCommandEight(View view) {
        SERModuleSerialTest.messageSendCommandEight();
    }
    public void messageSendCommand60(View view) {
        SERModuleSerialTest.messageSendCommand60();
    }

    public void messageSendStatusHELO(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.HELO));
    }
    public void messageSendStatusACK(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.ACK));
    }
    public void messageSendStatusNACK(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.NACK));
    }
    public void messageSendStatusFAIL(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.FAIL));
    }
    public void messageSendStatusDEBUG_ON(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_ON));
    }
    public void messageSendStatusDEBUG_OFF(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_OFF));
    }
    public void messageSendStatusPOWER_UP(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_POWER_UP));
    }
    public void messageSendStatusPOWER_DOWN(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_POWER_DOWN));
    }
    public void messageSendStatusRESET_CPU(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_RESET_CPU));
    }
    public void messageSendStatusRESET_TO_DEFAULTS(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_RESET_TO_DEFAULTS));
    }
    public void messageSendStatusERASE_EEPROM(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_ERASE_EEPROM));
    }

    public void messagetestServoSendButton(View view) {
        SERModuleSerialTest.setServoToValue(
                Integer.valueOf (((TextView) findViewById(R.id.testServoNumber)).getText().toString()),
                Integer.valueOf (((TextView) findViewById(R.id.testServoValue)).getText().toString())
                );
    }

    public void testSendAllTominClicked(View view) {
        SERModuleSerialTest.moveAllToMin();
    }
    public void testSendAllTomaxClicked(View view) {
        SERModuleSerialTest.moveAllToMax();
    }

    public void testSendBaseTominClicked(View view) {
        SERModuleSerialTest.moveBaseToMin();
    }
    public void testSendBaseTomaxClicked(View view) {
        SERModuleSerialTest.moveBaseToMax();
    }

    public void testSendShoulderTominClicked(View view) {
        SERModuleSerialTest.moveShoulderToMin();
    }
    public void testSendShoulderTomaxClicked(View view) {
        SERModuleSerialTest.moveShoulderToMax();
    }

    public void testSendElbowTominClicked(View view) {
        SERModuleSerialTest.moveElbowToMin();
    }
    public void testSendElbowTomaxClicked(View view) {
        SERModuleSerialTest.moveElbowToMax();
    }

}
