package com.lu.portable.detect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HomeListener {
    static final String TAG = "HomeListener";
    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerRecevier mRecevier;

    public HomeListener(Context paramContext) {
        this.mContext = paramContext;
        this.mFilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    }

    public void setOnHomePressedListener(OnHomePressedListener paramOnHomePressedListener) {
        this.mListener = paramOnHomePressedListener;
        this.mRecevier = new InnerRecevier();
    }

    public void startWatch() {
        if (this.mRecevier != null)
            this.mContext.registerReceiver(this.mRecevier, this.mFilter);
    }

    public void stopWatch() {
        if (this.mRecevier != null)
            this.mContext.unregisterReceiver(this.mRecevier);
    }

    public interface OnHomePressedListener {
        void onHomeLongPressed();

        void onHomePressed();
    }

    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        InnerRecevier() {
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            if (paramIntent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String string = paramIntent.getStringExtra("reason");
                if ((paramContext != null) && (HomeListener.this.mListener != null)) {
                    if (string.equals("homekey")) {
                        HomeListener.this.mListener.onHomePressed();
                    } else if (string.equals("recentapps")) {
                        HomeListener.this.mListener.onHomeLongPressed();
                    }
                }
            }
        }
    }
}