package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.configtools.InstrumentConfig;
import com.lu.portable.detect.model.ScaleDevice;

import java.util.ArrayList;
import java.util.HashMap;

public class CommonCompenstationAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public static HashMap <String, Object> values = new HashMap <>();
    public CommonCompenstationAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.weight_fragment_list_item, parent, false);
        }
        TextView hint = convertView.findViewById(R.id.hint);
        EditText value = convertView.findViewById(R.id.edit);
        hint.setText(getTitle(position));
        value.setText(getSubTitle(position));
        if(values.get(getCode(position))==null) {
            values.put(getCode(position), getSubTitle(position));
        }else{
            Log.e("CompensationMenuAdaptor",(String) values.get(getCode(position)));
        }
        value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false){
                    values.put(getCode(position), value.getText().toString());
                }
            }
        });
        convertView.requestLayout();
        return convertView;
    }
}
