package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.ui.HomeActivity;
import com.lu.portable.detect.util.SharedPreferencesUtil;


import java.util.ArrayList;
import java.util.HashMap;

public class DeviceAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public DeviceAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }
    private void setBatteryDrawable(double battery, ImageView batteryImage) {
        if (battery< 20.0) {
            batteryImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_b1));
        } else if (battery < 40.0) {
            batteryImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_b2));
        } else if (battery < 80.0) {
            batteryImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_b3));
        } else  {
            batteryImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_b4));
        }
    }

    private void setSignalDrawable(double signal, ImageView signalImage) {
        if (signal < 30) {
            signalImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sd));
        } else if (signal < 60) {
            signalImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sz));
        } else {
            signalImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sg));
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_item_layout, parent, false);
        }
        TextView device_info = convertView.findViewById(R.id.device_info);
        ImageView deviceImage = convertView.findViewById(R.id.device_img);
        ImageView signalImage = convertView.findViewById(R.id.device_signal);
        ImageView batteryImage = convertView.findViewById(R.id.battery_image);
        if (position < SharedPreferencesUtil.getScaleMode()) {
           Log.d("DeviceAdaptor",InstrumentConfig.scaleArr[position].getAddress() +InstrumentConfig.scaleArr[position].getHeart());
                if (InstrumentConfig.instrumentDevice.getHeart()<=0 || InstrumentConfig.scaleArr[position].getHeart() <= 0) {
                    device_info.setText(InstrumentConfig.scaleArr[position].getName());
                    device_info.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                   // device_status.setVisibility(View.GONE);
                    signalImage.setVisibility(View.GONE);
                    batteryImage.setVisibility(View.GONE);
                    deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_car_scale_red));
                } else {
                    deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_car_scale));
                    device_info.setTextColor(Color.WHITE);
                    device_info.setText(InstrumentConfig.scaleArr[position].getName());
                    setBatteryDrawable(InstrumentConfig.scaleArr[position].getBattery(), batteryImage);
                    setSignalDrawable(InstrumentConfig.scaleArr[position].getSignal(), signalImage);
                  //  device_status.setVisibility(View.VISIBLE);
                    signalImage.setVisibility(View.VISIBLE);
                    batteryImage.setVisibility(View.VISIBLE);
                }
        } else if (position == SharedPreferencesUtil.getScaleMode()) {
            device_info.setText(mContext.getString(R.string.IDS_instrument));
            if (InstrumentConfig.instrumentDevice.getHeart() > 0) {
                deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_work_black_24dp));
                device_info.setTextColor(Color.WHITE);
               // Log.e("InstrumentConfig.instrumentDevice.getBattery",""+InstrumentConfig.instrumentDevice.getBattery());
                setBatteryDrawable(InstrumentConfig.instrumentDevice.getBattery(), batteryImage);
                batteryImage.setVisibility(View.VISIBLE);
            } else {
                deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_work_red_24dp));
                device_info.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                batteryImage.setVisibility(View.GONE);
            }
        } else if (position == SharedPreferencesUtil.getScaleMode() + 1) {
            device_info.setText(mContext.getString(R.string.IDS_camera));
            if (InstrumentConfig.instrumentDevice.getHeart()>0 && InstrumentConfig.cameraConnected) {
                deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_camera_car));
                device_info.setTextColor(Color.WHITE);
            } else {
                deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_camera_car_red));
                device_info.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }

        }else if (position == SharedPreferencesUtil.getScaleMode() + 2) {
            deviceImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_local_printshop_red_24dp));
            //  device_info.setText(R.string.IDS_out_printer);
            device_info.setText(mContext.getString(R.string.IDS_out_printer));
            device_info.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//            device_status.setVisibility(View.GONE);
            batteryImage.setVisibility(View.GONE);
            signalImage.setVisibility(View.GONE);
        }
        convertView.requestLayout();
        return convertView;
    }
}
