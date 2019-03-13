package ninja.symmetry.spider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ninja.symmetry.spider.serial.Modules.SERModuleBasicMotion;
import ninja.symmetry.spider.serial.Modules.SERModuleLegMinMaxValues;
import ninja.symmetry.spider.serial.Packets.SERPacket;
import ninja.symmetry.spider.serial.SERSerial;
import ninja.symmetry.spider.R;


public class setMinMaxValuesDialog extends Activity implements iMessageReceivableActivity{
    List<Integer> offsets = new ArrayList<Integer>();

    private static final int MIN = 0;
    private static final int MAX = 1;
    private static final int MID = 2;

    int selectedServo = 0;
    int currentIncrement = 1;
    int currentMinValue = 0;
    int currentMaxValue = 0;

    int currentDifference = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_min_max_values_dialog);
        for (int i = 0; i < 48; i++) {
            offsets.add(i);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SERSerial.getInstance().addActivityToHandler(this);
        getAllValues(null);
        receiveConnectionStateChange(SERSerial.getConnectedStatus());
        updateUI();
    }

    @Override protected void onDestroy () {
        SERSerial.getInstance().removeActivityToHandler(this);
        super.onDestroy();
    }

    public void btnNumberdownPressed(View view) {
        TextView textNumber = (TextView) findViewById(R.id.txtSetNumber);
        Integer number = Integer.parseInt(textNumber.getText().toString());
        number -= 1;
        if (number >= 0 && number < 24) {
            selectedServo = number;
            updateUI();
        }
    }

    public void btnNumberupPressed(View view) {
        TextView textNumber = (TextView) findViewById(R.id.txtSetNumber);
        Integer number = Integer.parseInt(textNumber.getText().toString());
        number += 1;
        if (number >= 0 && number < 24) {
            selectedServo = number;
            updateUI();
        }
    }

    public int legNumberFromServo(int servoNumber) {
        return (servoNumber / 3);

    }

    public int jointNumberFromServo(int servoNumber) {
        return servoNumber % 3;

    }

    public void getAllValues(View view) {
        SERModuleLegMinMaxValues.getAllTweakValues();
    }

    public void setAllValues(View view) {
        SERModuleLegMinMaxValues.setAllTweakValues(this.offsets);
    }

    public void setValuesAndMin(View view) {
        saveAndMove(MIN);
    }

    public void setValuesAndMax(View view) {
        saveAndMove(MAX);
    }

    public void setValuesAndMid(View view) {
        saveAndMove(MID);
    }



    public void updateMinMax() {
        if (offsets.size() > 0) {
            currentMinValue = offsets.get(selectedServo * 2);
            currentMaxValue = offsets.get(selectedServo * 2 + 1);
        }

        if (currentMinValue > currentMaxValue) {
            currentDifference = currentMinValue - currentMaxValue;
        }
        else {
            currentDifference = currentMaxValue - currentMinValue;
        }
    }

    public void updateUI() {
        updateMinMax();
        ((TextView) findViewById(R.id.txtSetNumber)).setText(String.valueOf(selectedServo));
        ((TextView) findViewById(R.id.txtMinAmount)).setText(String.valueOf(currentMinValue));
        ((TextView) findViewById(R.id.txtMaxAmount)).setText(String.valueOf(currentMaxValue));
        ((TextView) findViewById(R.id.txtIncrement)).setText(String.valueOf(currentIncrement));
        ((TextView) findViewById(R.id.txtSelectedServo)).setText(
                "leg:" + String.valueOf(legNumberFromServo(selectedServo)) +
                        " Joint: " + String.valueOf(jointNumberFromServo(selectedServo)));
        ((TextView) findViewById(R.id.txtDifference)).setText(String.valueOf(currentDifference));
    }

    public void btnSwitchPressed(View view ) {
        int tempMin = currentMinValue;
        currentMinValue = currentMaxValue;
        currentMaxValue = tempMin;
        offsets.set((selectedServo * 2), currentMinValue);
        offsets.set((selectedServo * 2) + 1, currentMaxValue);
        updateUI();
    }

    public void btnMinUpPressed(View view ){
        currentMinValue += currentIncrement;
        offsets.set((selectedServo * 2), currentMinValue);
        if (minMaxBound()) {
            currentMaxValue += currentIncrement;
            offsets.set((selectedServo * 2) + 1, currentMaxValue);
        }
        autoSend(MIN);
        updateUI();
    }

    public void btnMinDownPressed(View view ){
        currentMinValue -= currentIncrement;
        offsets.set((selectedServo * 2), currentMinValue);
        if (minMaxBound()) {
            currentMaxValue -= currentIncrement;
            offsets.set((selectedServo * 2) + 1, currentMaxValue);
        }
        autoSend(MIN);
        updateUI();
    }

    public void btnMaxUpPressed(View view ){
        currentMaxValue += currentIncrement;
        offsets.set((selectedServo * 2) + 1, currentMaxValue);
        if (minMaxBound()) {
            currentMinValue += currentIncrement;
            offsets.set((selectedServo * 2), currentMinValue);
        }
        autoSend(MAX);
        updateUI();
    }

    public void btnMaxDownPressed(View view ){
        currentMaxValue -= currentIncrement;
        offsets.set((selectedServo * 2) + 1, currentMaxValue);
        if (minMaxBound()) {
            currentMinValue -= currentIncrement;
            offsets.set((selectedServo * 2), currentMinValue);
        }
        autoSend(MAX);
        updateUI();
    }

    public void btnIncrememntUpPressed(View view ){
        if (currentIncrement <= 10) {
            currentIncrement ++;
        }

        updateUI();
    }

    public void btnIncrememntDownPressed(View view ){
        if (currentIncrement > 1) {
            currentIncrement --;
        }

        updateUI();
    }

    private boolean minMaxBound() {
        return (((CheckBox) findViewById(R.id.chkBindDifference)).isChecked());

    }

    private boolean autoSave() {
        return (((CheckBox) findViewById(R.id.chkAutoSave)).isChecked());

    }

    private boolean moveall() {
        return (((CheckBox) findViewById(R.id.chkMoveAll)).isChecked());

    }
    public void autoSend(int upDown) {
        autoSend(upDown, false);
    }
    public void saveAndMove(int upDown) {
        autoSend(upDown, true);
    }
    public void autoSend(int upDown, boolean override) {
        if (!(autoSave() || override)) return;


        if (upDown == MAX) {
            if (moveall()) {
                SERModuleLegMinMaxValues.setAllTweakValuesAndMax(this.offsets);
            }
            else {
                SERModuleLegMinMaxValues.setMaxValueFor(selectedServo, currentMinValue, currentMaxValue);
            }
        }
        else if (upDown == MID) {
            if (moveall()) {
                SERModuleLegMinMaxValues.setAllTweakValuesAndMid(this.offsets);
            }
            else {
                SERModuleLegMinMaxValues.setMidValueFor(selectedServo, currentMinValue, currentMaxValue);
            }
        }
        else { //MIN
            if (moveall()) {
                SERModuleLegMinMaxValues.setAllTweakValuesAndMin(this.offsets);
            }
            else {
                SERModuleLegMinMaxValues.setMinValueFor(selectedServo, currentMinValue, currentMaxValue);
            }
        }
        updateUI();
    }

    @Override
    public void receiveMessage(SERPacket packet) {
        if (packet.featureSuper() == SERModuleLegMinMaxValues.FEATURE_MIN_MAX) {
            switch (packet.feature) {
                case SERModuleLegMinMaxValues.FEATURE_MIN_MAX_SET_ALL:
                    offsets.clear();
                    for (Integer value : packet.databuffer) {
                        offsets.add(value);
                    }
                    break;
            }
        }

        updateUI();
    }
    public void receiveConnectionStateChange(boolean connected) {
        if (!connected)
            findViewById(R.id.tweakLayout).setBackgroundColor(getResources().getColor(R.color.disconnected));
        else
            findViewById(R.id.tweakLayout).setBackgroundColor(getResources().getColor(R.color.connected));
    }

    public void setEvenToMin(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_MIN);
    }
    public void setEvenToMax(View view) {
        SERModuleBasicMotion.setEvenToStance(SERModuleBasicMotion.SERVO_MAX);
    }
    public void setOddToMin(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_MIN);
    }
    public void setOddToMax(View view) {
        SERModuleBasicMotion.setOddToStance(SERModuleBasicMotion.SERVO_MAX);
    }
}
