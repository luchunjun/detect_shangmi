package com.lu.portable.detect.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.model.Config;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.suke.widget.SwitchButton;

public class OtherSettingActivity extends BaseActivity {
    private RadioButton android_plate;
    private Button btn;
    private RadioButton camera_plate;
    private CheckBox checkBoxLightScreen;
    private CheckBox checkBoxSoundAlarm;
    private CheckBox checkBoxVibrateAlarm;
    private RadioGroup plate_selector;
    private RadioButton radioButtonPrintAll;
    private RadioButton radioButtonPrintManual;
    private RadioButton radioButtonPrintOverRange;
    private RadioButton radioButtonWeighWidth330mm;
    private RadioButton radioButtonWeighWidth340mm;
    private RadioGroup radioGroupPrint;
    private RadioGroup radioGroupWeighWidth;
    private RadioButton video_plate;
    private SwitchButton voiceSwitch;

    private void loadOtherSettings() {
       // SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
        voiceSwitch.setChecked(SharedPreferencesUtil.getBoolean("voice", true));
        checkBoxLightScreen.setChecked(SharedPreferencesUtil.getBoolean("lightScreen", true));
        checkBoxSoundAlarm.setChecked(SharedPreferencesUtil.getBoolean("soundAlarm", true));
        checkBoxVibrateAlarm.setChecked(SharedPreferencesUtil.getBoolean("vibrateAlarm", true));
        int weightWithIndex = SharedPreferencesUtil.getInt("weighWidth", 0);
        Log.d("loadOtherSettings", "weightWithIndex" + weightWithIndex);
        if (weightWithIndex == 0) {
            radioButtonWeighWidth340mm.setChecked(true);
        } else {
            radioButtonWeighWidth330mm.setChecked(true);
        }
        int printSetIndex = SharedPreferencesUtil.getInt("printSet", 2);
        Log.d("loadOtherSettings", "printSet" + printSetIndex);
        switch (printSetIndex) {
            case 0:
                radioButtonPrintAll.setChecked(true);
            case 1:
                radioButtonPrintOverRange.setChecked(true);
                break;
            default:
                radioButtonPrintManual.setChecked(true);
                break;
        }
        int plateIndex = SharedPreferencesUtil.getInt("plate", 2);
        Log.d("loadOtherSettings", "plate" + plateIndex);
        switch (plateIndex) {
            case 0:
                camera_plate.setChecked(true);
            case 1:
                android_plate.setChecked(true);
                break;
            default:
                video_plate.setChecked(true);
                break;
        }
    }

    private void toastMessage(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(OtherSettingActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_other_setting);
        setTitle(R.string.title_activity_other_setting);
        voiceSwitch = findViewById(R.id.voiceSwitch);
        checkBoxLightScreen = findViewById(R.id.checkBoxLightScreen);
        checkBoxSoundAlarm = findViewById(R.id.checkBoxSoundAlarm);
        checkBoxVibrateAlarm = findViewById(R.id.checkBoxVibrateAlarm);
        radioGroupPrint = findViewById(R.id.radioGroupPrint);
        radioButtonPrintAll = findViewById(R.id.radioButtonPrintAll);
        radioButtonPrintOverRange = findViewById(R.id.radioButtonPrintOverRange);
        radioButtonPrintManual = (findViewById(R.id.radioButtonPrintManual));
        radioGroupWeighWidth = findViewById(R.id.radioGroupWeighWidth);
        radioButtonWeighWidth340mm = findViewById(R.id.radioButtonWeighWidth340mm);
        radioButtonWeighWidth330mm = findViewById(R.id.radioButtonWeighWidth330mm);
        plate_selector = findViewById(R.id.plate_selector);
        camera_plate = findViewById(R.id.camera_palte);
        android_plate = findViewById(R.id.android_camera);
        video_plate = findViewById(R.id.android_video);
        loadOtherSettings();
        voiceSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferencesUtil.setBoolean("voice", isChecked);

            }
        });
        checkBoxLightScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SharedPreferencesUtil.setBoolean("lightScreen", checked);
            }
        });
        checkBoxSoundAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SharedPreferencesUtil.setBoolean("soundAlarm", checked);
            }
        });
        checkBoxVibrateAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SharedPreferencesUtil.setBoolean("vibrateAlarm", checked);
            }
        });
        radioGroupPrint.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup paramRadioGroup, int mCheckedId) {
                int i = Config.getInt("share", "printSet", 0);
                int selectedMode;
                switch (mCheckedId) {
                    case R.id.radioButtonPrintAll:
                        selectedMode = 0;
                        break;
                    case R.id.radioButtonPrintManual:
                        selectedMode = 1;
                        break;
                    case R.id.radioButtonPrintOverRange:
                        selectedMode = 2;
                        break;
                    default:
                        selectedMode = i;
                        break;
                }
                SharedPreferencesUtil.setInt("printSet", selectedMode);
            }
        });
        radioGroupWeighWidth.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup paramRadioGroup, int mCheckedId) {
                int i = Config.getInt("share", "weighWidth", 0);
                int selectedMode;
                switch (mCheckedId) {
                    case R.id.radioButtonWeighWidth340mm:
                        selectedMode = 0;
                        break;
                    case R.id.radioButtonWeighWidth330mm:
                        selectedMode = 1;
                        break;
                    default:
                        selectedMode = i;
                        break;
                }
                SharedPreferencesUtil.setInt("weighWidth", selectedMode);
            }
        });
        plate_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup paramRadioGroup, @IdRes int id) {
                int i = Config.getInt("share", "plate", 0);
                int selectedMode;
                switch (id) {
                    case R.id.camera_palte:
                        selectedMode = 0;
                        break;
                    case R.id.android_camera:
                        selectedMode = 1;
                        break;
                    case R.id.android_video:
                        selectedMode = 2;
                        break;
                    default:
                        selectedMode = i;
                        break;
                }
                SharedPreferencesUtil.setInt("plate", selectedMode);
            }
        });
    }
}