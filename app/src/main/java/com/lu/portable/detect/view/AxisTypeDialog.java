package com.lu.portable.detect.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.Car;
import com.lu.portable.detect.util.AxisTypeListAdapter;
import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class AxisTypeDialog extends Dialog {
    private String axisType;
    private Context context;
    double currentLimit;
    private AxisTypeDialogListener listener = null;
    int icon[];
    int limit[];
    String types[];
    String names[];
    public AxisTypeDialog(Context context) {
        super(context);
        this.context = context;
        this.axisType = "";
    }

    private View setupView(String axisCountName, String axisTypes) {
        View allAxisTypeLayout = LayoutInflater.from(this.context).inflate(R.layout.all_axis_types, null);
        ListView localListView = allAxisTypeLayout.findViewById(R.id.view_container);
        Button cancelBtn = allAxisTypeLayout.findViewById(R.id.select_cancel);
        Button okBtn = allAxisTypeLayout.findViewById(R.id.select_ok);
        ArrayList <Map <String, Object>> data = new ArrayList <>();
        Log.e("AxisTypeDialog","axisTypes:"+axisTypes);
        if("t_3"== axisCountName){
            icon =Car.threeAxisesTypeIcons  ;
            limit =Car.threeLimit  ;
            types=Car.threeAxisesTypes;
            names =Car.threeLabel;
        }else if("t_4"== axisCountName){
            icon =Car.fourAxisesTypeIcons  ;
            limit =Car.fourLimit  ;
            types=Car.fourAxisesTypes;
            names =Car.fourLabel;
        }else if("t_5"== axisCountName){
            icon =Car.fiveAxisesTypeIcons  ;
            limit =Car.fiveLimit  ;
            types=Car.fiveAxisesTypes;
            names =Car.fiveLabel;
        }else{
            icon =Car.sixAxisesTypeIcons;
            limit =Car.sixLimit;
            types= Car.sixAxisesTypes;
            names =Car.sixLabel;
        }
        for (int i =0;i<types.length;i++) {
            Map <String, Object> hashMap = new HashMap <>();
            hashMap.put("img", icon[i]);
            hashMap.put("limit",limit[i]);
            hashMap.put("type", types[i]);
            hashMap.put("selected", axisTypes.equals(types[i]));
            hashMap.put("name",names[i]);
            data.add(hashMap);
        }
        AxisTypeListAdapter axisTypeListAdapter = new AxisTypeListAdapter(context, data);
        localListView.setAdapter(axisTypeListAdapter);
        axisTypeListAdapter.setListener((mChecked, position, type, mlimit) -> {
            axisType = type;
            Log.d("type", mChecked + "  " + position + " " + type + " " + mlimit);
            currentLimit = mlimit;
            InstrumentConfig.carTypeName= names[position];
            Log.e("axisTypeListAdapter",names[position]);
            InstrumentConfig.carTypeImage = icon[position];
            Log.e("axisTypeListAdapter","fff"+icon[position]);
        });

        okBtn.setOnClickListener(view -> {
            if (AxisTypeDialog.this.listener != null) {
                AxisTypeDialog.this.listener.positiveClick(axisType, currentLimit);
            }
            AxisTypeDialog.this.dismiss();
        });
        cancelBtn.setOnClickListener(view -> {
            if (AxisTypeDialog.this.listener != null) {
                AxisTypeDialog.this.listener.cancelClick();
            }
            AxisTypeDialog.this.dismiss();
        });
        return allAxisTypeLayout;
    }


    public void setListener(AxisTypeDialogListener paramAxisTypeDialogListener) {
        this.listener = paramAxisTypeDialogListener;
    }

    public void setView(String axisType, String axisDetailType) {
        super.setContentView(setupView(axisType, axisDetailType));
    }

    public  interface AxisTypeDialogListener {
        void cancelClick();

        void positiveClick(String paramString, double limit);
    }
}