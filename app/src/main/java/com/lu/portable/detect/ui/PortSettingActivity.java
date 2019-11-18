package com.lu.portable.detect.ui;


import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.util.SharedPreferencesUtil;

public class PortSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_other_layout);
        setTitle(R.string.other_setting);
        initView();
    }
    private void initView(){
//        RadioButton instrumentRadio = findViewById(R.id.instrument_connect);
//        RadioButton cameraRadio = findViewById(R.id.camera_connect);
        RadioButton cameraPlateRadio = findViewById(R.id.camera_car_plate);
        RadioButton takePhotoRadio = findViewById(R.id.take_photo_plate);
//        if(8040==SharedPreferencesUtil.getScalePort()){
//            cameraRadio.setChecked(true);
//        }else{
//            instrumentRadio.setChecked(true);
//        }
        if(0==SharedPreferencesUtil.getPlateMethod()){
            cameraPlateRadio.setChecked(true);
        }else{
            takePhotoRadio.setChecked(true);
        }
        Button savePortBtn = findViewById(R.id.saveConnectPort);
        savePortBtn.setOnClickListener(v -> {
//            if(instrumentRadio.isChecked()){
//                SharedPreferencesUtil.setScalePort(8020);
//            }else{
//                SharedPreferencesUtil.setScalePort(8040);
//            }
            if(cameraPlateRadio.isChecked()){
                SharedPreferencesUtil.setPlateMethod(0);
            }else{
                SharedPreferencesUtil.setPlateMethod(1);
            }
            finish();
        });
    }

}
