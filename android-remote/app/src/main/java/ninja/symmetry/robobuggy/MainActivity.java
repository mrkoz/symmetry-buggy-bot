package ninja.symmetry.robobuggy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ninja.symmetry.robobuggy.serial.Packets.SERPacket;
import ninja.symmetry.robobuggy.serial.SERReceiveHandler;
import ninja.symmetry.robobuggy.serial.SERSerial;

public class MainActivity extends roboActivity {
    private SERReceiveHandler rHandler = new SERReceiveHandler(this, SERSerial.getInstance());

    public Intent intentJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentJoystick = new Intent(this, DrivingJoystick.class);
    }

    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.MainActivityLayout).setBackgroundColor(getResources().getColor(R.color.disconnected, getTheme()));
        else
            findViewById(R.id.MainActivityLayout).setBackgroundColor(getResources().getColor(R.color.connected, getTheme()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return (id == R.id.action_settings || super.onOptionsItemSelected(item));
    }

    public void connectSerial(View view) {
        String deviceName = ((TextView) findViewById(R.id.txtDeviceName)).getText().toString();
        SERSerial.set_device_name(deviceName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.txtDeviceName)).setText(SERSerial.get_device_name());
    }

    /* various debug things */
    public void messageSendStatusDebugOn(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_ON));
    }
    public void messageSendStatusDebugOff(View view) {
        SERSerial.getInstance().sendMessage(new SERPacket(SERPacket.STATUS_DEBUG_OFF));
    }

    public void popupJoystick(View view) {
        startActivity(intentJoystick);
    }

}
