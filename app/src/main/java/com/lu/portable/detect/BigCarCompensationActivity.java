package com.lu.portable.detect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.adaptor.IconMenuAdaptor;
import com.lu.portable.detect.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class BigCarCompensationActivity extends BaseActivity {

   EditText big1;
   EditText big2;
   EditText big3;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_compensation_big_car);
        setTitle(R.string.big_car_weight_compensation);
        initView();
    }
    private void initView(){
        big1 =findViewById(R.id.big_car_1_edit);
        big2 =findViewById(R.id.big_car_2_edit);
        big3 =findViewById(R.id.big_car_3_edit);
        big1.setText(""+SharedPreferencesUtil.getFactorInt("big_car_1_weight",100));
        big2.setText(""+SharedPreferencesUtil.getFactorInt("big_car_2_weight",100));
        big3.setText(""+SharedPreferencesUtil.getFactorInt("big_car_3_weight",100));
    }
    public  void saveCompensation(View view){
        SharedPreferencesUtil.setInt("big_car_1_weight",Integer.parseInt(big1.getText().toString()));
        SharedPreferencesUtil.setInt("big_car_2_weight",Integer.parseInt(big2.getText().toString()));
        SharedPreferencesUtil.setInt("big_car_3_weight",Integer.parseInt(big3.getText().toString()));
        showToast(R.string.save_success);
        finish();
    }
    public  void startSpeedCompensation(View view){
      startActivity(new Intent(this,CompensationSpeedFactorActivity.class));
      finish();
    }

}