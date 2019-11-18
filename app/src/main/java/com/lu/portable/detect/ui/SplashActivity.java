package com.lu.portable.detect.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.util.SharedPreferencesUtil;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
//    private Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            start();
//        }
//
//        ;
//    };


    private void start() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }


    /**
     * decide start first guide page or login page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        start();
        TextView copyrightTextiew = findViewById(R.id.copyrighttextview);
        copyrightTextiew.setText(String.format(getString(R.string.copy_string), SharedPreferencesUtil.getCompanyName()));
        // UserUtil.login(this, "13571874922", "e10adc3949ba59abbe56e057f20f883e");//just for test
        //verifyStoragePermissions();
//        new Thread() {
//            public void run() {
//                try {
//                    Thread.sleep(100);
//                    handler.sendEmptyMessage(0);
//                } catch (InterruptedException e) {
//                    Log.d(TAG, e.toString());
//                    handler.sendEmptyMessage(0);
//                }
//            }
//
//            ;
//        }.start();
    }
}
