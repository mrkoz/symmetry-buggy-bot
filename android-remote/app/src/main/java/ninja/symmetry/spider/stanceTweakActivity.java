package ninja.symmetry.spider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import ninja.symmetry.spider.serial.Modules.SERModuleStanceTweak;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;
import ninja.symmetry.spider.R;

public class stanceTweakActivity extends Activity implements iMessageReceivableActivity {

    public int increase = 1;
    public int stanceSelected = 0;
    public int stanceCopySelected = 0;
    public int legSelected = 0;
    public String[] legs = {"0&1","2&3","4&5","6&7"};

    //values
    public multiArrayList stanceValues = new multiArrayList(SERModuleStanceTweak.STANCES_COUNT, 24);
    public boolean[] stanceReflect ={ false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false, };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stance_tweak);
        ((Spinner) findViewById(R.id.spinnerStanceSetting)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                stanceSelected = position;
                updateValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        ((Spinner) findViewById(R.id.spinnerLegSelection)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                legSelected = position;
                updateValues();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        ((Spinner) findViewById(R.id.spinnerStanceCopy)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                stanceCopySelected = position;
                updateValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        stanceValues.createDummyData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        updateSpinner();
        receiveConnectionStateChange(SERSerial.getConnectedStatus());

    }

    @Override protected void onDestroy () {
        SERSerial.getInstance().removeActivityToHandler(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stance_tweak, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (item.getItemId() == R.id.action_settings ||  super.onOptionsItemSelected(item));
    }

    public void updateSpinner() {
        Spinner spinnerStance = (Spinner)findViewById(R.id.spinnerStanceSetting);
        ArrayAdapter<String> spinnerStanceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SERModuleStanceTweak.stances);
        spinnerStance.setAdapter(spinnerStanceAdapter);

        Spinner spinnerLeg = (Spinner)findViewById(R.id.spinnerLegSelection);
        ArrayAdapter<String> spinnerLegAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, legs);
        spinnerLeg.setAdapter(spinnerLegAdapter);

        Spinner spinnerStanceCopy = (Spinner)findViewById(R.id.spinnerStanceCopy);
        ArrayAdapter<String> spinnerStanceCopyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SERModuleStanceTweak.stances);
        spinnerStanceCopy.setAdapter(spinnerStanceCopyAdapter);
    }

    @Override
    public void receiveMessage(SERPacket packet) {
        if (packet.featureSuper() == SERModuleStanceTweak.FEATURE_STANCE) {
            int updateStance = packet.get_next_value();
            Log.i("STANCES", String.format("Stance retrieved %d (%s)", updateStance, SERModuleStanceTweak.stances[updateStance]));
            stanceValues.getListAt(updateStance).clear();
            stanceReflect[updateStance] = (packet.get_next_value() == 1);
            for (int i = 0; i < 24; i++) {
                stanceValues.getListAt(updateStance).add(packet.get_next_value());
            }
            updateValues();
        }
    }
    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.stanceLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.stanceLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    public void updateValues() {
        int offset = legSelected * 6;
        ((TextView) findViewById(R.id.txtView0Base)).setText(String.format(Locale.ENGLISH, "%d - Base", (legSelected * 2)));
        ((TextView) findViewById(R.id.txtView0Lift)).setText(String.format(Locale.ENGLISH, "%d - Lift", (legSelected * 2)));
        ((TextView) findViewById(R.id.txtView0Elbow)).setText(String.format(Locale.ENGLISH, "%d - Elbow", (legSelected * 2)));
        ((TextView) findViewById(R.id.txtView1Base)).setText(String.format(Locale.ENGLISH, "%d - Base", (legSelected * 2) + 1));
        ((TextView) findViewById(R.id.txtView1Lift)).setText(String.format(Locale.ENGLISH, "%d - Lift", (legSelected * 2) + 1));
        ((TextView) findViewById(R.id.txtView1Elbow)).setText(String.format(Locale.ENGLISH, "%d - Elbow", (legSelected * 2) + 1));
        ((EditText) findViewById(R.id.txt0Base)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset)));
        ((EditText) findViewById(R.id.txt0Lift)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset + 1)));
        ((EditText) findViewById(R.id.txt0Elbow)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset + 2)));
        ((EditText) findViewById(R.id.txt1Base)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset + 3)));
        ((EditText) findViewById(R.id.txt1Lift)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset + 4)));
        ((EditText) findViewById(R.id.txt1Elbow)).setText(String.valueOf(stanceValues.getValueAt(stanceSelected, offset + 5)));
    }

    public void getValues(View view) {
        SERModuleStanceTweak.getStanceTweak(stanceSelected);
    }

    public void getAllValues(View view) {
        SERModuleStanceTweak.getAllStanceTweaks();
    }

    public void sendAndSaveValues(View view) {
        Log.i("STANCES", String.format("Saving stance %d", stanceSelected));
        SERModuleStanceTweak.setStanceTweak(stanceSelected, stanceValues.getListAt(stanceSelected), stanceReflect[stanceSelected]);
    }

    public void toggleReflect(View view) {
    }

    public void btnIncrementPressed(View view) {
        int value;
        switch (view.getId()) {
            case R.id.btnMidBasePlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Base)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt0Base)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnMidBaseMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Base)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt0Base)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnMidLiftPlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Lift)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt0Lift)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnMidLiftMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Lift)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt0Lift)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnMidElbowPlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Elbow)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt0Elbow)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnMidElbowMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt0Elbow)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt0Elbow)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndBasePlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Base)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt1Base)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndBaseMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Base)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt1Base)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndLiftPlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Lift)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt1Lift)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndLiftMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Lift)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt1Lift)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndElbowPlusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Elbow)).getText().toString()) + increase;
                ((TextView) findViewById(R.id.txt1Elbow)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnEndElbowMinusOne:
                value = Integer.parseInt(((TextView) findViewById(R.id.txt1Elbow)).getText().toString()) - increase;
                ((TextView) findViewById(R.id.txt1Elbow)).setText(String.valueOf(value));
                setValues();
                break;
            case R.id.btnIncreasePlusOne:
                increase += 1;
                ((TextView) findViewById(R.id.txtIncrease)).setText(String.valueOf(increase));
                break;
            case R.id.btnIncreaseMinusOne:
                increase -= 1;
                ((TextView) findViewById(R.id.txtIncrease)).setText(String.valueOf(increase));
                break;
        }
    }

    public void setValues() {
        int value0 = Integer.parseInt(((TextView) findViewById(R.id.txt0Base)).getText().toString());
        int value1 = Integer.parseInt(((TextView) findViewById(R.id.txt0Lift)).getText().toString());
        int value2 = Integer.parseInt(((TextView) findViewById(R.id.txt0Elbow)).getText().toString());
        int value3 = Integer.parseInt(((TextView) findViewById(R.id.txt1Base)).getText().toString());
        int value4 = Integer.parseInt(((TextView) findViewById(R.id.txt1Lift)).getText().toString());
        int value5 = Integer.parseInt(((TextView) findViewById(R.id.txt1Elbow)).getText().toString());
        if (!((ToggleButton) findViewById(R.id.toggleReflect)).isChecked()) {
            int offset = legSelected * 6;

            stanceValues.setValueAt(stanceSelected, 0 + offset, value0);
            stanceValues.setValueAt(stanceSelected, 1 + offset, value1);
            stanceValues.setValueAt(stanceSelected, 2 + offset, value2);
            stanceValues.setValueAt(stanceSelected, 3 + offset, value3);
            stanceValues.setValueAt(stanceSelected, 4 + offset, value4);
            stanceValues.setValueAt(stanceSelected, 5 + offset, value5);
        }
        else {

            int valueMidBase, valueMidLift, valueMidElbow;
            int valueEndBase, valueEndLift, valueEndElbow;

            //are we working with the front or rear? (front == (0||3)
            if (legSelected == 0 || legSelected == 3) { // = front
                valueEndBase = value0;
                valueEndLift = value1;
                valueEndElbow = value2;
                valueMidBase = value3;
                valueMidLift = value4;
                valueMidElbow = value5;
            }
            else {
                valueMidBase = value0;
                valueMidLift = value1;
                valueMidElbow = value2;
                valueEndBase = value3;
                valueEndLift = value4;
                valueEndElbow = value5;
            }

            //leg 0 -- end
            stanceValues.setValueAt(stanceSelected, 0, valueEndBase);
            stanceValues.setValueAt(stanceSelected, 1, valueEndLift);
            stanceValues.setValueAt(stanceSelected, 2, valueEndElbow);

            //leg 1 -- mid
            stanceValues.setValueAt(stanceSelected, 3, valueMidBase);
            stanceValues.setValueAt(stanceSelected, 4, valueMidLift);
            stanceValues.setValueAt(stanceSelected, 5, valueMidElbow);

            //leg 2 -- mid
            stanceValues.setValueAt(stanceSelected, 6, valueMidBase);
            stanceValues.setValueAt(stanceSelected, 7, valueMidLift);
            stanceValues.setValueAt(stanceSelected, 8, valueMidElbow);

            //leg 3 -- end
            stanceValues.setValueAt(stanceSelected, 9, valueEndBase);
            stanceValues.setValueAt(stanceSelected, 10, valueEndLift);
            stanceValues.setValueAt(stanceSelected, 11, valueEndElbow);

            //leg 4 -- end
            stanceValues.setValueAt(stanceSelected, 12, valueEndBase);
            stanceValues.setValueAt(stanceSelected, 13, valueEndLift);
            stanceValues.setValueAt(stanceSelected, 14, valueEndElbow);

            //leg 5 -- mid
            stanceValues.setValueAt(stanceSelected, 15, valueMidBase);
            stanceValues.setValueAt(stanceSelected, 16, valueMidLift);
            stanceValues.setValueAt(stanceSelected, 17, valueMidElbow);

            //leg 6 -- mid
            stanceValues.setValueAt(stanceSelected, 18, valueMidBase);
            stanceValues.setValueAt(stanceSelected, 19, valueMidLift);
            stanceValues.setValueAt(stanceSelected, 20, valueMidElbow);

            //leg 7 -- end
            stanceValues.setValueAt(stanceSelected, 21, valueEndBase);
            stanceValues.setValueAt(stanceSelected, 22, valueEndLift);
            stanceValues.setValueAt(stanceSelected, 23, valueEndElbow);
        }
    }

    public void copySettingsFromStance(View view) {
        stanceValues.duplicateListFromTo(stanceCopySelected, stanceSelected);
        updateValues();
    }
}
