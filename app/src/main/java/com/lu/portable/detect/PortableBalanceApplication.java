package com.lu.portable.detect;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.kernal.demo.plateid.R;
import com.lu.portable.detect.sunmi.AidlUtil;

public class PortableBalanceApplication extends MultiDexApplication {
    private  static final String TAG ="PortableBalanceApplication";
    private static PortableBalanceApplication _instance;
    private static long userId ;
    private static String userName;
    private boolean isAidl;

    public boolean isAidl() {
        return isAidl;
    }

    public void setAidl(boolean aidl) {
        isAidl = aidl;
    }
    public static PortableBalanceApplication getAppContext() {
        return _instance;
    }

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long userID) {
        userId = userID;
    }
    public static void setUserName(String name) {
        userName = name;
    }
    public static String getUserName() {
        return userName;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        super.onCreate();
        AidlUtil.getInstance().connectPrinterService(this);
        initVoice();
    }

    private void initVoice(){
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        //Log.e(TAG,"initVoice");
        param.append(SpeechConstant.ENGINE_MODE+"="+ SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(PortableBalanceApplication.this, param.toString());
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void onTerminate() {
        super.onTerminate();
    }
}