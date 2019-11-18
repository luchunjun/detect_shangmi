package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DetectRecordAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public static final String PLATE_NUM = "PLATE_NUM";
    public static final String TIME = "TIME";
    public static final String AXIS_NUM = "AXIS_NUM";
    public static final String WEIGHT = "WEIGHT";
    public DetectRecordAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.detect_record_item_layout, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.plateNum=convertView.findViewById(R.id.plate_number);
            viewHolder.time=convertView.findViewById(R.id.record_time);
            viewHolder.axisNum=convertView.findViewById(R.id.axis_num);
            viewHolder.weight=convertView.findViewById(R.id.weight);
            viewHolder.checkBox=convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        try {
            viewHolder.plateNum.setText(getString(position,PLATE_NUM));
            viewHolder.time.setText(getString(position,TIME));
            viewHolder.axisNum.setText(getString(position,AXIS_NUM));
            viewHolder.weight.setText(getString(position,WEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.checkBox.setChecked(getSwitch(position));
        convertView.requestLayout();
        return convertView;
    }

 public   class ViewHolder{
      public TextView plateNum;
      public TextView time;
      public TextView axisNum;
      public TextView weight;
      public  CheckBox checkBox;
    }
}
