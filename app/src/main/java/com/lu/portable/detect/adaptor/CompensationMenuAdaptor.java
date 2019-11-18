package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CompensationMenuAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public static HashMap <String, Object> values = new HashMap <>();

    public CompensationMenuAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.compensation_view_layout, parent, false);
        }
        TextView title = convertView.findViewById(R.id.compensationTitle);
        ImageView imageView = convertView.findViewById(R.id.truckImage);
        final EditText editText = convertView.findViewById(R.id.compensationValue);
        title.setText(getTitle(position));
        editText.setText(getSubTitle(position));
        if(values.get(getCode(position))==null) {
            values.put(getCode(position), getSubTitle(position));
        }else{
            Log.e("CompensationMenuAdaptor",(String) values.get(getCode(position)));
        }

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                values.put(getCode(position), editText.getText().toString());
              //  Log.e("mData",mData.toString());
            }
        });

        if (getImage(position) != null) {
            imageView.setImageDrawable(getImage(position));
        } else {
            imageView.setVisibility(View.GONE);
        }
        convertView.requestLayout();
        return convertView;
    }
}
