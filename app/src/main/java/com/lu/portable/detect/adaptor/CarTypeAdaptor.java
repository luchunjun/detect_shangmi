package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CarTypeAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public CarTypeAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.car_type_list_item, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.iv_icon);
        TextView title = convertView.findViewById(R.id.title);
        if (getImage(position) != null) {
            imageView.setImageDrawable(getImage(position));
        } else {
            imageView.setVisibility(View.GONE);
        }
        title.setText(getSubTitle(position));
        convertView.requestLayout();
        return convertView;
    }
}
