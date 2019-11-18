package com.lu.portable.detect.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lu.portable.detect.PortableBalanceApplication;
import com.kernal.demo.plateid.R;

public class SharedPreferencesUtil {
    public final static String USER_NAME_TAG = "userName";
    public final static int TRUE_TAG = 1;
    private final static SharedPreferences mSharedPreferences = PortableBalanceApplication.getAppContext()
            .getSharedPreferences("detect", Context.MODE_MULTI_PROCESS);
    private final static String USER_ID_TAG = "USER_ID";
    private final static String USER_PASSWORD_TAG = "USER_PASSWORD";
    private final static String IS_LOGIN_TAG = "IS_LOGIN";
    private final static String IS_LAUNCH_TAG = "IS_LAUNCH";
    private final static String COMPANY_TAG = "COMPANY_TAG";
    private final static String PLACE_TAG = "PLACE_TAG";
    private final static String PLOLICY_TAG = "PLOLICY_TAG";
    private final static String SCALE_MODE_TAG = "SCALE_MODE_TAG";
    private final static String AXIS_NUM_TAG = "AXIS_NUM_TAG";
    private final static String SCALE_CONNNECCT_PORT_TAG = "SCALE_CONNNECCT_PORT_TAG";
    private final static String PLATE_METHOD = "PLATE_METHOD";

    public static void setPlateMethod(int method) {
        setInt(PLATE_METHOD, method);
    }

    public static int getPlateMethod() {
        return mSharedPreferences.getInt(PLATE_METHOD, 0);
    }

    public static void setScalePort(int port) {
        setInt(SCALE_CONNNECCT_PORT_TAG, port);
    }

    public static int getScalePort() {
        return mSharedPreferences.getInt(SCALE_CONNNECCT_PORT_TAG, 8020);
    }

    public static void setAxisNum(int axisNum) {
        setInt(AXIS_NUM_TAG, axisNum);
    }

    public static int getAxisNum() {

        return mSharedPreferences.getInt(AXIS_NUM_TAG, 0);
    }

    public static String getUserId() {
        return mSharedPreferences.getString(USER_ID_TAG, "");
    }

    public static void setUserId(String userId) {
        setString(USER_ID_TAG, userId);
    }

    public static String getCompanyName() {
        return mSharedPreferences.getString(COMPANY_TAG, PortableBalanceApplication.getAppContext().getString(R.string.default_company_name));
    }

    public static void setCompanyName(String companyName) {
        setString(COMPANY_TAG, companyName);
    }

    public static String getPolicyName() {
        return mSharedPreferences.getString(PLOLICY_TAG, "");
    }

    public static void setPolicyName(String policyName) {
        setString(PLOLICY_TAG, policyName);
    }

    public static String getPlaceName() {
        return mSharedPreferences.getString(PLACE_TAG, "");
    }

    public static void setPlaceName(String placeName) {
        setString(PLACE_TAG, placeName);
    }

    public static int getScaleMode() {
        return mSharedPreferences.getInt(SCALE_MODE_TAG, 2);
    }

    public static void setScaleMode(int mode) {
        setInt(SCALE_MODE_TAG, mode);
    }

    public static String getUserName() {
        return mSharedPreferences.getString(USER_NAME_TAG, "");
    }

    public static void setUserName(String userName) {
        setString(USER_NAME_TAG, userName);
    }

    public static String getUserPassword() {
        return mSharedPreferences.getString(USER_PASSWORD_TAG, "");
    }

    public static void setUserPassword(String userPassword) {
        setString(USER_PASSWORD_TAG, userPassword);
    }

    public static boolean isLogin() {
        return mSharedPreferences.getBoolean(IS_LOGIN_TAG, false);
    }

    public static void setLogin(Boolean isLogin) {
        setBoolean(IS_LOGIN_TAG, isLogin);
    }

    public static boolean isLaunch() {
        return mSharedPreferences.getBoolean(IS_LAUNCH_TAG, false);
    }

    public static void setLaunch(Boolean isLaunch) {
        setBoolean(IS_LAUNCH_TAG, isLaunch);
    }

    public static void setBoolean(String tag, Boolean value) {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(tag, value);
        editor.apply();
    }

    public static boolean getBoolean(String tag, boolean defaultValue) {
        return mSharedPreferences.getBoolean(tag, defaultValue);
    }

    public static int getInt(String tag, int defaultVaue) {
        return mSharedPreferences.getInt(tag, defaultVaue);
    }

    public static void setString(String tag, String value) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public static void setLong(String tag, Long value) {
        Editor editor = mSharedPreferences.edit();
        editor.putLong(tag, value);
        editor.apply();
    }

    public static void setInt(String tag, int value) {
        Editor editor = mSharedPreferences.edit();
        editor.putInt(tag, value);
        editor.apply();
    }

    public static String getFactorInt(String key) {
        return String.valueOf(mSharedPreferences.getInt(key, 100));
    }

    public static int getFactor(String key) {
        return mSharedPreferences.getInt(key, 100);
    }

    public static int getFactorInt(String key, int defautValue) {
        return mSharedPreferences.getInt(key, defautValue);
    }

}
