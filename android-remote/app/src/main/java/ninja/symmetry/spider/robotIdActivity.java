package ninja.symmetry.spider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ninja.symmetry.spider.serial.Modules.SERModuleId;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;
import ninja.symmetry.spider.R;


public class robotIdActivity extends Activity implements iMessageReceivableActivity {

  String currentName;
  int currentId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_robot_id);

    ((Spinner) findViewById(R.id.identitySpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        currentId = position;
        updateUI();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
      }

    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    SERSerial.getInstance().addActivityToHandler(this);
    getAllValues(null);
    receiveConnectionStateChange(SERSerial.getConnectedStatus());
    updateSpinner();
  }

  @Override
  protected void onDestroy() {
    SERSerial.getInstance().removeActivityToHandler(this);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_robot_id, menu);
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
    if (packet.featureSuper() == SERPacket.FEATURE_ID) {
      switch (packet.feature) {
        case SERModuleId.FEATURE_ID_SET_IDENTITY:
          currentId = packet.databuffer.get(0);
          currentName = SERModuleId.getNameFromId(currentId);
          updateUI();
          break;
      }
    }
  }
  public void receiveConnectionStateChange(boolean connected) {
    if (!connected)
      findViewById(R.id.robotIDlayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
    else
      findViewById(R.id.robotIDlayout).setBackgroundColor(getResources().getColor(R.color.connected));
  }

  public void updateSpinner() {
    Spinner dropdown = (Spinner) findViewById(R.id.identitySpinner);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SERModuleId.robotNames);
    dropdown.setAdapter(adapter);
  }

  public void updateUI() {
    currentName = SERModuleId.getNameFromId(currentId);
    ((TextView) findViewById(R.id.txtName)).setText(currentName);
  }

  public void getAllValues(View view) {
    SERModuleId.messageGetIdentity();
  }

  public void setId(View view) {
    SERModuleId.messageSetIdentity(currentId);
  }

  public void displayName(View view) {
    SERModuleId.messageDisplayIdentity();
  }

  public void resetToDefaults(View view) {
    SERModuleId.messageResetToDefaultsIdentity();
  }


}
