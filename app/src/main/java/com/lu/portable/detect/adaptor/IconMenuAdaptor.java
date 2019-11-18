package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class IconMenuAdaptor extends CommonAdapter <HashMap <String, Object>> {
    public IconMenuAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.icon_menu_item, parent, false);
        }
        TextView title = convertView.findViewById(R.id.tv_title);
        ImageView imageView = convertView.findViewById(R.id.iv_icon);
        title.setText(getTitle(position));
        if (getImage(position) != null) {
            imageView.setImageDrawable(getImage(position));
        } else {
            imageView.setVisibility(View.GONE);
        }
        convertView.requestLayout();
        return convertView;
    }
}
