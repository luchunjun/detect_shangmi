package com.lu.portable.detect.model;

import android.content.SharedPreferences;

import com.lu.portable.detect.util.JsonHelper;

public class Config {
    public static final String FILE_CONFIG = "file";
    public static final String SHARE_CONFIG = "share";
    private static Config _instance = new Config();
    private JsonHelper fileConfig;
    private SharedPreferences sharedPreferences;

    public static boolean Contains(String paramString1, String paramString2) {
        return _instance.fileConfig.contains(paramString2);
    }

    public static boolean getBoolean(String paramString1, String paramString2, boolean paramBoolean) {
//        int i = -1;
//        switch (paramString1.hashCode())
//        {
//            default:
//                switch (i)
//                {
//                    default:
//                    case 0:
//                    case 1:
//                }
//            case 3143036:
//            case 109400031:
//        }
//        do
//        {
//            return paramBoolean;
//            if (!paramString1.equals("file"))
//                break;
//            i = 0;
//            break;
//            if (!paramString1.equals("share"))
//                break;
//            i = 1;
//            break;
//        }
//        while (_instance.fileConfig == null);
//        return _instance.fileConfig.getBoolean(paramString2, paramBoolean);
        // return _instance.sharedPreferences.getBoolean(paramString2, paramBoolean);
        return false;
    }

    public static int getInt(String paramString1, String paramString2, int paramInt) {
        int i = -1;
        switch (paramString1.hashCode()) {
            default:
                switch (i) {
                    default:
                    case 0:
                    case 1:
                }
            case 3143036:
            case 109400031:
        }
//        do
//        {
//            return paramInt;
//            if (!paramString1.equals("file"))
//                break;
//            i = 0;
//            break;
//            if (!paramString1.equals("share"))
//                break;
//            i = 1;
//            break;
//        }
//        while (_instance.fileConfig == null);
//        return _instance.fileConfig.getInt(paramString2, paramInt);
        // return _instance.sharedPreferences.getInt(paramString2, paramInt);
        return 0;
    }

    public static String getString(String paramString1, String paramString2, String paramString3) {
        int i = -1;
//        switch (paramString1.hashCode()) {
//            default:
//                switch (i) {
//                    default:
//                    case 0:
//                    case 1:
//                }
//            case 3143036:
//            case 109400031:
//        }
//        do
//        {
//            return paramString3;
//            if (!paramString1.equals("file"))
//                break;
//            i = 0;
//            break;
//            if (!paramString1.equals("share"))
//                break;
//            i = 1;
//            break;
//        }
//        if (_instance.fileConfig == null) {
//            return _instance.fileConfig.getString(paramString2, paramString3);
//        } else {
//            return _instance.sharedPreferences.getString(paramString2, paramString3);
//        }
        return "";
    }

    public static Config newInstance() {
        return _instance;
    }

    public static void setFileConfig(JsonHelper config) {
        _instance.fileConfig = config;
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        _instance.sharedPreferences = sharedPreferences;
    }
}