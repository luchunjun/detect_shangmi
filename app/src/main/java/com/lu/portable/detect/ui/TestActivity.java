package com.lu.portable.detect.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lu.portable.detect.BaseActivity;
import com.lu.portable.detect.SocketActivity;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.kernal.demo.plateid.R;

//ctrl+alt+o
public class TestActivity extends SocketActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_test_info);
        setTitle(R.string.test_info);

    }

    @Override
    protected void onResume() {
        super.onResume();
        testStatus =true;
    }
    @Override
    protected void onPause(){
        super.onPause();
        testStatus =false;
    }

    public void onClick(View view){
        Log.e("TestActivity","onClick");
        switch (view.getId()){
            case R.id.start1:
                sendTestCheck("01");
                break;
            case R.id.start2:
                sendTestCheck("02");
                break;
            case R.id.start3:
                sendTestCheck("03");
                break;
            case R.id.start4:
                sendTestCheck("04");
                break;
            case R.id.scale1:
                sendTestSleep("01");
                break;
            case R.id.scale2:
                sendTestSleep("02");
                break;
            case R.id.scale3:
                sendTestSleep("03");
                break;
            case R.id.scale4:
                sendTestSleep("04");
                break;
        }
    }

}