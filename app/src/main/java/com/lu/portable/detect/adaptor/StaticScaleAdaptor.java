package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.ScaleDevice;
import com.lu.portable.detect.util.LogUtil;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticScaleAdaptor extends CommonAdapter <HashMap <String, Object>> {


    public StaticScaleAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.static_scale_item_layout, parent, false);
        }
        TextView scale_name = convertView.findViewById(R.id.scale_name);
        TextView load_value = convertView.findViewById(R.id.load_value);
        LinearLayout weight_layout  =convertView.findViewById(R.id.weight_layout);
        ScaleDevice scaleDevice = InstrumentConfig.scaleArr[position];
        if(scaleDevice.getHeart()>0){
            scale_name.setText( scaleDevice.getName()+ mContext.getString(R.string.IDS_inner_code)+scaleDevice.getInnerCode());
            weight_layout.setVisibility(View.VISIBLE);
            load_value.setText(""+scaleDevice.getWeight());
           // LogUtil.e("weight", mContext.getString(R.string.IDS_inner_code)+scaleDevice.getInnerCode() +"weight:"+scaleDevice.getWeight()+"##"+scaleDevice.getAddress());
        }else{
            weight_layout.setVisibility(View.GONE);
            weight_layout.setVisibility(View.GONE);
            scale_name.setText(String.format(mContext.getString(R.string.IDS_scale_with_order), scaleDevice.getName()) + mContext.getString(R.string.IDS_not_connected));
        }
        convertView.requestLayout();
        return convertView;
    }
}
