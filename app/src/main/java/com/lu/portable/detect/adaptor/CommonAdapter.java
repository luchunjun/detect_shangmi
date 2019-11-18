package com.lu.portable.detect.adaptor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lu.portable.detect.interfaces.CommonCallback;
import com.lu.portable.detect.util.NewThreadUpdateUIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    public final static int BEGIN_INDEX = 0;
    public static final String TITLE = "title";
    public static final String SUB_TITLE = "subTitle";
    public static final String IMAGE = "image";
    public static final String CODE = "code";
    public final static String SWITCH = "switch";
    public final static String BTN = "btn";
    public final static String TYPE = "type";
    public List <HashMap <String, Object>> mData;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected CheckChangeListener mCheckChangeListener;

    public CommonAdapter(ArrayList <HashMap <String, Object>> list, Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = list;
    }

    public void setCheckChangeListener(CheckChangeListener listener) {
        mCheckChangeListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getTitle(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return "unKnow";
        } else {
            return (String) lHashMap.get(TITLE);
        }
    }

    public String getCode(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return "unKnow";
        } else {
            return (String) lHashMap.get(CODE);
        }
    }

    public int getType(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        return (int) lHashMap.get(TYPE);

    }

    public Drawable getImage(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return null;
        } else {
            return (Drawable) lHashMap.get(IMAGE);
        }
    }

    public String getSubTitle(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return "unKnown";
        } else {
            return (String) lHashMap.get(SUB_TITLE);
        }
    }
    public String getString(int position,String key) {
        HashMap <String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return "unKnown";
        } else {
            return (String) lHashMap.get(key);
        }
    }
    public HashMap <String, Object> extracted(int position) {
        try {
            return mData.get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

/*    public Bitmap getBitmap(int position) {
        HashMap<String, Object> lHashMap = extracted(position);
        if (lHashMap == null) {
            return null;
        } else {
            return (Bitmap) lHashMap.get(IMAGE);
        }
    }*/

    public boolean getSwitch(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        return lHashMap.get(SWITCH) != null && (boolean) lHashMap.get(SWITCH);
    }

    public boolean getButton(int position) {
        HashMap <String, Object> lHashMap = extracted(position);
        return lHashMap.get(BTN) != null && (boolean) lHashMap.get(BTN);
    }

    public void addNewData(final ArrayList <HashMap <String, Object>> data) {
        NewThreadUpdateUIUtil.newThreadUpdateUI(new CommonCallback() {
            @Override
            public void handle() {
                mData.addAll(data);
                notifyDataSetChanged();
            }
        });

    }

    public void remove(int postion) {
        if (postion < mData.size()) {
            mData.remove(postion);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        NewThreadUpdateUIUtil.newThreadUpdateUI(new CommonCallback() {
            @Override
            public void handle() {
                mData.clear();
                notifyDataSetChanged();
            }
        });
    }

    public interface CheckChangeListener {
        void onCheckedChange(CommonAdapter adapter, int position);
    }

    public class ViewHolder {
        protected TextView title;
        protected TextView subTitle;
        protected ImageView imageView;
        protected Button runBtn;
        protected View warnView;
        RadioButton radioBtn;
        EditText editText;
    }
}
