package com.lu.portable.detect.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.kernal.demo.plateid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AxisTypeListAdapter extends BaseAdapter {
    private Map<String, ViewHolder> allViewHolder;
    private ArrayList<Map<String, Object>> axisAllTypes;
    private OnItemSelectListener listener;
    private LayoutInflater mInflater;
    public static int currentSelect=-1;

    public AxisTypeListAdapter(Context mcontext, ArrayList<Map<String, Object>> data) {
        axisAllTypes = new ArrayList();
        axisAllTypes.addAll(data);
        mInflater = LayoutInflater.from(mcontext);
        allViewHolder = new HashMap();
        currentSelect=-1;
    }

    public void addViewHolder(String key, ViewHolder holder) {
        if (allViewHolder.get(key) == null) {
            allViewHolder.put(key, holder);
        }
    }

    public int getCount() {
        return axisAllTypes.size();
    }

    public Object getItem(int position) {
        return axisAllTypes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() != null) {
            convertView = mInflater.inflate(R.layout.axis_type, parent, false);
            holder = new ViewHolder();
            holder.img = convertView.findViewById(R.id.axis_type_view);
            holder.selectRadio = convertView.findViewById(R.id.select_radio);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img.setBackgroundResource((int) axisAllTypes.get(position).get("img"));
        holder.limit = (int) axisAllTypes.get(position).get("limit");
        if (currentSelect==-1) {
            holder.isselectd = (Boolean) (axisAllTypes.get(position)).get("selected");
        }else {
            if(position ==currentSelect){
                holder.isselectd =true;
            }else{
                holder.isselectd =false;
            }
        }
        holder.selectRadio.setChecked(holder.isselectd);
        holder.selectRadio.setText(axisAllTypes.get(position).get("name")+"限重" + holder.limit + "t");
        holder.selectRadio.setOnCheckedChangeListener(new RadioChildClickListener(position, (String) axisAllTypes.get(position).get("type")));
        addViewHolder((String) axisAllTypes.get(position).get("type"), holder);
        convertView.requestLayout();
        return convertView;
    }

    public void setListener(OnItemSelectListener onItemSelectListener) {
        listener = onItemSelectListener;
    }

    public   interface OnItemSelectListener {
        void OnItemSelectOnItemSelect(boolean mChecked, int position, String type, double mlimit);
    }

    class ViewHolder {
        public ImageView img;
        public boolean isselectd = false;
        public int limit;
        public CheckBox selectRadio;
    }

    class RadioChildClickListener implements CompoundButton.OnCheckedChangeListener {
        private String name;
        private int position;

        public RadioChildClickListener(int position, String name) {
            this.position = position;
            this.name = name;
        }

        public void onCheckedChanged(CompoundButton paramCompoundButton, boolean mChecked) {
            if (mChecked) {
                currentSelect = position;
                notifyDataSetChanged();
                listener.OnItemSelectOnItemSelect(mChecked, position, name, allViewHolder.get(name).limit);
            }
        }
    }

}


