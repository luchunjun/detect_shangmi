package com.lu.portable.detect;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.adaptor.StaticScaleAdaptor;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.ScaleDevice;
import com.lu.portable.detect.util.SharedPreferencesUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class StaticWeightingActivity extends SocketActivity {
    private final static String TAG = "StaticWeightingActivity";
    private final Queue<ScaleDevice> scaleDeviceQueue = new ArrayDeque<>();
    ListView deviceListView;
    Handler handler = new Handler();
    StaticScaleAdaptor deviceAdaptor;
    // 60000次------------

    private void initListView() {
        deviceListView = findViewById(R.id.list_view);
        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < SharedPreferencesUtil.getScaleMode(); i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            data.add(hashMap);
        }
        deviceAdaptor = new StaticScaleAdaptor(data, this);
        deviceListView.setAdapter(deviceAdaptor);
    }

    public void updateListData() {
        deviceAdaptor.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_static_calibration);
        setTitle(R.string.static_calibration_title);
        initListView();
        if (!InstrumentConfig.isScaleConnected()) {
            showToast(R.string.IDS_no_scale);
        } else {
            //sendContinueMine();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        staticWeightStatus = true;
        //sendContinueMine();
            startContinueMine();
    }

    @Override
    public void onPause() {
        super.onPause();
        staticWeightStatus = false;
        handler.removeCallbacks(mRunnable);
        sendStopMine();
    }

    private void startContinueMine() {
        if(InstrumentConfig.noScaleOnline()) {
            Log.e(TAG,"startContinueMine:"+InstrumentConfig.noScaleOnline());
            handler.postDelayed(mNoSCaleRunnable, 5000);
        }else{
            for (ScaleDevice scaleDevice : InstrumentConfig.scaleArr) {
                scaleDevice.setZeroInnerCode(0L);
                scaleDevice.setIndex(0);
                if (scaleDevice.getHeart() > 0) {
                    scaleDeviceQueue.add(scaleDevice);
                }
            }
            if (scaleDeviceQueue.peek() != null) {
                sendContinueMine(scaleDeviceQueue.peek());
            }
        }
    }

    private void sendContinueMine(final ScaleDevice scaleDevice) {
            new Thread(() -> sendScaleMsg(InstrumentConfig.generateSendMsg(scaleDevice.getAddress(), "F2", InstrumentConfig.CONTINUE_DATA_ACQUISITION))).start();
            handler.postDelayed(mRunnable, 500);
    }
    Runnable mNoSCaleRunnable = () -> {
        startContinueMine();
    };
    Runnable mRunnable = () -> {
        scaleDeviceQueue.add(scaleDeviceQueue.peek());
        sendTestSleep(scaleDeviceQueue.peek().getAddress());
        scaleDeviceQueue.add(scaleDeviceQueue.peek());
        scaleDeviceQueue.poll();
        sendContinueMine(scaleDeviceQueue.peek());
        updateListData();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendStopMine();
        handler.removeCallbacks(mRunnable);
        handler.removeCallbacks(mNoSCaleRunnable);
        handler = null;
        finish();
    }
}