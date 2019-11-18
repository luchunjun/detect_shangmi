package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteUserAdaptor extends CommonAdapter <HashMap <String, Object>> {

    public DeleteUserAdaptor(ArrayList <HashMap <String, Object>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.delete_user_item, parent, false);
            viewHolder=new ViewHolder();
            viewHolder.title=convertView.findViewById(R.id.username);
            viewHolder.checkBox=convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.title.setText(getTitle(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.checkBox.setChecked(getSwitch(position));
        convertView.requestLayout();
        return convertView;
    }

 public static  class ViewHolder{
     public TextView title;
      public  CheckBox checkBox;
    }
}
