package com.lu.portable.detect.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.interfaces.CommonCallback;
import com.kernal.demo.plateid.R;


public class NewThreadUpdateUIUtil {
    public static void newThreadUpdateUI(final CommonCallback callBack) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.handle();
            }
        });
    }
    public static void newThreadUpdateUI(final CommonCallback callBack,long postDelay) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.handle();
            }
        },postDelay);
    }
    public static void showToast(final int strId) {
        showToast(PortableBalanceApplication.getAppContext().getString(strId));
    }

    public static void showToast(final String str) {

        newThreadUpdateUI(new CommonCallback() {
            @Override
            public void handle() {
                Toast.makeText(PortableBalanceApplication.getAppContext(), str, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showSuccess() {
        showToast(R.string.IDS_common_success);
    }

    public static void showFail() {
        showToast(R.string.IDS_common_fail);
    }
}
