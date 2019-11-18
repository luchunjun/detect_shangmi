package com.lu.portable.detect.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.adaptor.DeviceAdaptor;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.ui.HomeActivity;
import com.lu.portable.detect.util.LogUtil;
import com.lu.portable.detect.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DetectFragment extends BaseFragment {
    final static String TAG = "DetectFragment";
    public static Button cancelButton;
    public static Button cameraBtn;
    public static DetectFragment detectFragment;
    private static ImageView hintLight;
    DeviceAdaptor deviceAdaptor;
    HomeActivity homeActivity;
    private Button manualStopButton;
    private Button manualCheckButton;

    public static DetectFragment newInstance() {
        if (detectFragment == null) {
            detectFragment = new DetectFragment();
        }
        return detectFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detectFragmentView = inflater.inflate(R.layout.fragment_detect, container, false);
        homeActivity = (HomeActivity) getActivity();
        setupViews(detectFragmentView);
        return detectFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (InstrumentConfig.cameraHasElect) {
            cameraBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            cameraBtn.setTextColor(getResources().getColor(R.color.theme_accent));
        }
    }

    private void closeInstrument() {
        LinearLayout disableLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.sensor_disable, null);
        TextView sensorNameTv = disableLayout.findViewById(R.id.sensor_name);
        sensorNameTv.setText(getString(R.string.IDS_close_instrument_hint));
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.IDS_instrument)).setView(disableLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                homeActivity.closeInstrument();
                LogUtil.d(TAG, "closeInstrument");
            }
        }).setNegativeButton(R.string.IDS_common_cancel, null).show();
    }

    private void closeSensor(final int position) {
        LinearLayout disableLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.sensor_disable, null);
        TextView sensorNameTv = disableLayout.findViewById(R.id.sensor_name);
        sensorNameTv.setText(String.format(getString(R.string.IDS_close_scale_hints), InstrumentConfig.scaleArr[position].getName()));
        new AlertDialog.Builder(getActivity()).setTitle(String.format(getString(R.string.IDS_scale_with_order), InstrumentConfig.scaleArr[position].getName())).setView(disableLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                homeActivity.closeScale(position);
            }
        }).setNegativeButton(R.string.IDS_common_cancel, null).show();
    }


    private void initListView(View parent) {
        GridView deviceListView = parent.findViewById(R.id.device_list_view);
        LinearLayout bigCarLayout = parent.findViewById(R.id.big_car_type_layout);
        Spinner spinner = parent.findViewById(R.id.big_car_type);
        ImageView bigCarTypePic = parent.findViewById(R.id.big_car_type_pic);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InstrumentConfig.bigCarType = position;
                switch (position) {
                    case 0:
                        bigCarTypePic.setImageResource(R.mipmap.big_car_1);
                        InstrumentConfig.carType ="big_car_1";
                        InstrumentConfig.carTypeImage=R.mipmap.big_car_1;
                        InstrumentConfig.carTypeName=getString(R.string.big_car_1);
                        break;
                    case 1:
                        bigCarTypePic.setImageResource(R.mipmap.big_car_2);
                        InstrumentConfig.carType ="big_car_2";
                        InstrumentConfig.carTypeImage=R.mipmap.big_car_2;
                        InstrumentConfig.carTypeName=getString(R.string.big_car_2);
                        break;
                    case 2:
                        bigCarTypePic.setImageResource(R.mipmap.big_car_3);
                        InstrumentConfig.carTypeImage=R.mipmap.big_car_3;
                        InstrumentConfig.carTypeName=getString(R.string.big_car_3);
                        InstrumentConfig.carType ="big_car_3";
                        break;
                    default:
                        bigCarTypePic.setImageResource(R.mipmap.big_car_1);
                        InstrumentConfig.carType ="big_car_1";
                        InstrumentConfig.carTypeImage=R.mipmap.big_car_1;
                        InstrumentConfig.carTypeName=getString(R.string.big_car_1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (SharedPreferencesUtil.getScaleMode() == 4) {
            bigCarLayout.setVisibility(View.VISIBLE);
        } else {
            bigCarLayout.setVisibility(View.GONE);
        }

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();
        int deviceCount = SharedPreferencesUtil.getScaleMode() + 3;
        for (int i = 0; i < deviceCount; i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            data.add(hashMap);
        }
        deviceAdaptor = new DeviceAdaptor(data, getActivity());
        deviceListView.setAdapter(deviceAdaptor);
        deviceListView.setOnItemClickListener((parent1, view, position, id) -> {
            Log.d("deviceListView", "onClick" + position);
            if (position < SharedPreferencesUtil.getScaleMode()) {
                if (InstrumentConfig.scaleArr[position].getHeart() > 0) {
                    closeSensor(position);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.IDS_not_connected_scale), Toast.LENGTH_SHORT).show();
                }
            } else if (position == SharedPreferencesUtil.getScaleMode()) {
                Log.d("close instrument", "close 1!!!!");
                if (InstrumentConfig.instrumentDevice.getHeart() > 0) {
                    closeInstrument();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.IDS_not_connected_instrument), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void updateList() {
        if (deviceAdaptor != null) {
            deviceAdaptor.notifyDataSetChanged();
        }
    }

    public void playHightLight() {
        hintLight.setBackgroundResource(R.drawable.circle_solid_yellow);
        new Handler().postDelayed(greenhintRunnable , 1500);
    }

    Runnable  greenhintRunnable = new Runnable(){
        @Override
        public void run() {
            hintLight.setBackgroundResource(R.drawable.circle_solid_green);
        }
    };
    private void setupViews(View parent) {
        initListView(parent);
        hintLight = parent.findViewById(R.id.hintLight);
        cameraBtn = parent.findViewById(R.id.controlCameraPower);
        manualCheckButton = parent.findViewById(R.id.manualCheckButton);//start
        manualStopButton = parent.findViewById(R.id.manualStopButton);
        cancelButton = parent.findViewById(R.id.clear);
        manualCheckButton.setEnabled(true);
        cancelButton.setEnabled(false);
        manualStopButton.setEnabled(false);
        manualCheckButton.setOnClickListener(view -> {
            if (InstrumentConfig.instrumentDevice.getHeart() <= 0) {
                homeActivity.showToast("没有连接上仪表");
                return;
            }
            if (!InstrumentConfig.isScaleConnected()) {
                homeActivity.showToast("没有连接上称台");
                return;
            }
            homeActivity.startCheck();
            cancelButton.setEnabled(true);
            manualStopButton.setEnabled(true);
            manualCheckButton.setEnabled(false);
        });
        manualStopButton.setOnClickListener(paramView -> {
            manualStopButton.setEnabled(false);
            cancelButton.setEnabled(false);
            manualCheckButton.setEnabled(true);
            homeActivity.stopCheck();
        });
        cancelButton.setOnClickListener(view -> {
            manualStopButton.setEnabled(false);
            cancelButton.setEnabled(false);
            manualCheckButton.setEnabled(true);
            homeActivity.cancelCheck();
        });
    }
}
