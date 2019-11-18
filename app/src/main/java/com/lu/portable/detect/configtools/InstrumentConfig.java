package com.lu.portable.detect.configtools;

import android.util.Log;

import com.kernal.demo.plateid.R;
import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.model.CommonDevice;
import com.lu.portable.detect.model.ScaleDevice;
import com.lu.portable.detect.model.StaticValueListForScale;
import com.lu.portable.detect.util.CompensationJsonHelper;
import com.lu.portable.detect.util.LogUtil;
import com.lu.portable.detect.util.SharedPreferencesUtil;
import com.lu.portable.detect.util.SocketUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class InstrumentConfig {
    //alt+command +L
    public static final String INSTRUMENT_IP = "192.168.88.99";
    public static boolean isLogin = false;
    public static boolean isCameraPowerLoginClose = false;
    public static final String TEST_IP = "192.168.88.102";
    public static final int TEST_PORT = 8080;

    public static final int CAMERA_LIGHT_PORT = 8030;

    public static final String OPEN_CAMERA_LIGHT = "AF AF AF AF 00 00";
    public static final String CLOSE_CAMERA_LIGHT = "FA FA FA FA 00 00";
    public static final String CLOSE_CAMERA_POWER = "AF AF AF FA 00 00";
    public static final String OPEN_CAMERA_POWER = "AF AF AF AF 00 00";

    //55 03 F1 70 73 6F 66 00 00 AC 5A
    //55 04 F1 70 73 6F 66 00 00 AD 5A
    // public static final int INSTRUMENT_PORT = 8080;
    public static final int INSTRUMENT_PORT = 8010;
    //public static final int SCALE_PORT = 8081;
    public static final int SCALE_PORT = 8020;

    // public static final String CLOSE_SOURCE_COMMAND = "FA AF AF AF 00 00";
    public static final String FRAME_HEADER = "55";
    public static final String FRAME_TAIL = "5A";
    //55 01 f2 06 52 43 53 46 00 00 27 5a ”RCSF” 停止采集成功。
    public static final String RCSF = "F206524353460000";
    public static final String[] COMMAND_TYPE = {"F1", "F2", "F3", "F4", "F5"};
    public static final String[] HEART_BEATS = {"21", "40", "23", "24", "25", "5E", "26", "2A", "28"};
    public static final String[] ADDRESS_ARR = {"01", "02", "03", "04", "05", "06", "07", "08", "09"};
    private static final String[] SIGNAL_ARR = {"02", "03", "04", "05", "04", "05", "00", "00", "00"};
    private static final String[] FOUR_SCALE_ADDRESS_ARR = {"01", "02", "03", "04"};
    public static ArrayList<String> resultAddressList = new ArrayList<>();
    // 下位机发送: “55 adr F2 06 43 44 44 54 00 00 ** 5A”：”CDDT” 通知上位机开始接收数据
    //CDDT
    public static final String NOTIFY_PHONE_RECEIVE_DATA = "F206434444540000";
    public static final String HEART_HEAD = "6874";
    //55 01 f3 06 ab bc cd ea 72 74 18 5a 55 计算轴数已满
    public static final String AXIS_FULL = "F106ABBCCDEA7274";
    //public static final String AXIS_FULL1 = "F1 06 AB BC CD EA 72 74";
    //“55 adr F1 06 61 6B 6B 70 00 00 ** 5A”  ask keep open or not 30
    public static final String CAMERA_ADDRESS = "03";
    public static final String ASK_OPEN_OR_NOT = "616B6B700000";

    public static final String CLOSE = "70 73 6F 66 00 00";
    private static final String CLOSE_SCALE = "06 70 73 6F 66 00 00";
    //“DE DF EF DA 20 02”
    //“DE DF EF DA dd” DEDFEFDA20029F //10
    public static final String SINGAL_HEAD = "DEDFEFDA20";
    public static final String RECEIVE_SINGAL_HEAD = "DEDFEFDA";
    public static final String MINE_CONTINUE_DATA = "F306";
    //“55 adr F1 06 6B 70 6F 6E 00 00 ** 5A”
    public static final String KEEP_OPEN = "6B 70 6F 6E 00 00";
    //public static final String KEEP_SCALE_OPEN = "06 6B 70 6F 6E 00 00";
    //55 adr F2 06 70 6C 73 70 00 00 ** 5A reply for sleep “55 adr F2 06 52 43 53 46 00 00 ** 5A” ：”RCSF”
    public static final String SLEEP = "06 70 6C 73 70 00 00";
    //55 adr F2 06 73 64 61 71 00 00 ** 5A for static weight   reply“55 adr F2 06 53 44 44 54 00 00 ** 5A”：”SDDT”
    public static final String SDDT = "06534444540000";
    public static final String CONTINUE_DATA_ACQUISITION = "06 73 64 61 71 00 00";
    //back 06 43 44 44 54
    public static final String CALCULATE_DATA_ACQUISITION = "06 43 41 4C 43 44 44"; //55 01 F2 06 43 41 4C 43 44 44 94 5A
    //”55 adr F2 06 54 53 42 46 00 00 ** 5A”:” TSBV” test battery reply
    public static final String TSBV = "06 54 53 42 46 00 00";
    public static final String TSBV_INSTRUMENT = "54 53 42 46 00 00";

    public static final String THRESHOLD_SET = "06 74 68 00 01 0B 05";
    //public static final String THRESHOLD_SET = "06 74 68 00 00 00 00";
    // ”55 adr F2 06 DD DD DD DD 6F 6B ** 5A”:” DDDDDDOK”
    // public static final String GOOD_THRESHOLD = "6F6B";
    // ”55 adr F2 06 DD DD DD DD 5A 52 ** 5A”: DDDDDDZR” ZERO
    // public static final String BAD_THRESHOLD = "5A52";
    public static final String WEIGHT_ERROR_AXIS = "F3066572726F7274";
    //“55 adr F3 74 69 6D 65 4f 4c ** 5A”： time OL 超时报错
    public static final String WEIGHT_OVER_TIME = "F30674696D654F4C";
    private static final String TAG = "InstrumentConfig";
    public static StaticValueListForScale staticValueListForScaleArr[] = {new StaticValueListForScale(), new StaticValueListForScale(), new StaticValueListForScale(), new StaticValueListForScale()};
    public static ScaleDevice[] scaleArr = {new ScaleDevice(), new ScaleDevice(), new ScaleDevice(), new ScaleDevice()};
    public static boolean cameraConnected = false;
    public static boolean cameraHasElect = true;
    public static int startCamera = 0;
    final static int scale_2[] = {5, 10, 16, 22};
    final static int scale_3[] = {8, 16, 24, 32};
    final static int scale_4[] = {11, 22, 32, 43};
    final static int scale_5[] = {13, 26, 39, 52};
    final static int scale_6[] = {15, 30, 44, 59};
    //”55 adr F2 06 74 68 DD DD DD DD ** 5A”:” thst” reply with ”55 adr F2 06 DD DD DD DD 6F 6B ** 5A”:” DDDDDDOK”
    // 42 46 00 00
    //19 b0 ed 00 00 30 05
    public static String carPlateNumPhoto = "";
    public static String carPlateNumFullPhoto = "";
    public static CommonDevice instrumentDevice = new CommonDevice("04");
    public static CommonDevice cameraDevice = new CommonDevice("03");
    public static String carPlate = "未识别车牌";
    // “55 adr F306 65 72 72 6f 72 74 ** 5A”： erro rt 轴数报错
    public static boolean isCheckbtnClick = false;
    private static CompensationJsonHelper compensationJsonHelper;
    public static int bigCarType = 0;
    public static String carType = "";
    public static String carTypeName = "";
    public static int carTypeImage = -1;
    public static Vector<Double> axisWeights = new Vector<>();
    public static String generateSendMsg(String address, String command, String data) {
        if (address == null && address.equals("")) {
            return "";
        } else {
            String msg = FRAME_HEADER + address + command + data.replace(" ", "") + SocketUtil.checkSum(address, command, data) + FRAME_TAIL;
            Log.d(TAG, "generateSendMsg" + msg);
            return msg;
        }
    }

    private static double getSpeedRatio(double speed) {
        try {
                String key ="";
                if (speed < 3.0D) {
                    Log.e(TAG, "cf_speedbelow3" + SharedPreferencesUtil.getFactor("cf_speedbelow3"));
                  //  return SharedPreferencesUtil.getFactor("cf_speedbelow3") / 100.0D;
                    key ="cf_speedbelow3";
                } else if (speed <= 4.0D) {
                    Log.e(TAG, "cf_speed3_4" + SharedPreferencesUtil.getFactor("cf_speed3_4"));
                    //return SharedPreferencesUtil.getFactor("cf_speed3_4") / 100.0D;
                    key ="cf_speed3_4";
                } else if (speed <= 5.0D) {
                    Log.e(TAG, "cf_speed4_5" + SharedPreferencesUtil.getFactor("cf_speed4_5"));
                   // return SharedPreferencesUtil.getFactor("cf_speed4_5") / 100.0D;
                    key ="cf_speed4_5";
                } else if (speed <= 6.0D) {
                    Log.e(TAG, "cf_speed5_6" + SharedPreferencesUtil.getFactor("cf_speed5_6"));
                    //return SharedPreferencesUtil.getFactor("cf_speed5_6") / 100.0D;
                    key ="cf_speed5_6";
                } else if (speed <= 8.0D) {
                    Log.e(TAG, "cf_speed6_8" + SharedPreferencesUtil.getFactor("cf_speed6_8"));
                    //return SharedPreferencesUtil.getFactor("cf_speed6_8") / 100.0D;
                    key ="cf_speed6_8";
                } else {
                    Log.e(TAG, "cf_speedabove8" + SharedPreferencesUtil.getFactor("cf_speedabove8"));
                   // return SharedPreferencesUtil.getFactor("cf_speedabove8") / 100.0D;
                    key ="cf_speedabove8";
                }
                if(SharedPreferencesUtil.getScaleMode()==4){
                     return SharedPreferencesUtil.getFactor(key+"_big") / 100.0D;
                }else{
                    return SharedPreferencesUtil.getFactor(key) / 100.0D;
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100 / 100.0D;
    }

    public static double getCarWeight() {
        if (InstrumentConfig.getAxisNum() < 2) {
            return 0.00d;
        }
        double realWeight;
        Vector<Double> scaleWeights = new Vector<>();
         axisWeights.clear();
        for (ScaleDevice device : scaleArr) {
            if (device.getHeart() > 0) {
                double scaleWeight = 0.0;
                for (int i = 0; i < device.peakArrayList.size(); i++) {
                    // ConfigTools.writeLog(device.getName()+"峰值数据:"+device.peakArrayList.get(i));
                    double peakWeight = getWeightDetect(device, device.peakArrayList.get(i)) * getSpeedRatio(device.speedArrayList.get(i));
                    String key="";
                    if (SharedPreferencesUtil.getScaleMode() == 2 && getAxisNum() > 2) {
                        if (i == 0)
                        {
                            if(carType.equals("t3_2_1")||carType.equals("t4_1_1_2_0")|| carType.equals("t5_1_1_1_1_1")) {
                                key =carType + "_2l";
                                Log.e(TAG,"key:"+key);
                                peakWeight = peakWeight *  getRatiosByKey(key);
                            }else if(carType.equals("t5_1_1_1_2") || carType.equals("t5_1_1_1_2_s") || carType.equals("t6_1_1_1_3")){
                                key =carType + "_3l";
                            }
                        }else if (i == 1) {
                            if(carType.equals("t3_2_1")||carType.equals("t4_1_1_2_0")|| carType.equals("t5_1_1_1_1_1")){
                                key =carType + "_2l";
                                peakWeight = peakWeight *  getRatiosByKey(key);
                            }else if (carType.equals("t3_1_2") || carType.equals("t4_1_2_1")
                                    || carType.equals("t5_1_2_2") || carType.equals("t5_1_2_2_s") || carType.equals("t5_1_2_1_1")
                                    || carType.equals("t6_1_2_3") || carType.equals("t6_1_2_3_49") || carType.equals("t6_1_2_3_s")
                                    || carType.equals("t6_1_2_3_s_49") || carType.equals("t6_1_2_3_s_49")) {
                                key =carType + "_2c";
                            }else if(carType.equals("t5_1_1_1_2") || carType.equals("t5_1_1_1_2_s") || carType.equals("t6_1_1_1_3")){
                                key =carType + "_3l";
                            }

                        }else if (i == 2) {
                            if (carType.equals("t3_1_2") || carType.equals("t4_1_2_1")||carType.equals("t4_1_1_2_0")||carType.equals("t4_1_1_2_s")
                                    ||carType.equals("t4_1_1_2") || carType.equals("t5_1_2_2") || carType.equals("t5_1_2_2_s")
                                    || carType.equals("t5_1_2_1_1") || carType.equals("t6_1_2_3") || carType.equals("t6_1_2_3_49")
                                    || carType.equals("t6_1_2_3_s") || carType.equals("t6_1_2_3_s_49") || carType.equals("t6_1_2_3_s_49")
                                    || carType.equals("t6_1_1_2_1_1")  || carType.equals("t6_1_1_2_1_1_49") )
                            {
                                key =carType + "_2c";
                            }else if(carType.equals("t5_1_1_3")){
                                key =carType + "_3c";
                            }else if(carType.equals("t5_1_1_1_2") || carType.equals("t5_1_1_1_2_s") || carType.equals("t6_1_1_1_3")){
                                key =carType + "_3l";
                            }
                        }else if (i == 3) {
                            if (carType.equals("t4_1_1_2_0")||carType.equals("t4_1_1_2_s")||carType.equals("t4_1_1_2")
                                    || carType.equals("t5_1_2_2") || carType.equals("t5_1_2_2_s")
                                    || carType.equals("t5_1_1_1_2")|| carType.equals("t5_1_1_1_2_s")
                                    || carType.equals("t6_1_1_2_2") || carType.equals("t6_1_1_2_2_49")
                                    || carType.equals("t6_1_1_2_1_1")  || carType.equals("t6_1_1_2_1_1_49")) {
                                key =carType + "_2c";
                            }else if(carType.equals("t5_1_1_3")|| carType.equals("t6_1_2_3") || carType.equals("t6_1_2_3_49") || carType.equals("t6_1_1_1_3")
                                    || carType.equals("t6_1_2_3_s") || carType.equals("t6_1_2_3_s_49")){
                                key =carType + "_3c";
                            }

                        }else if(i==4){//compensation the fifth axis
                            if (carType.equals("t5_1_2_2") || carType.equals("t5_1_2_2_s")
                                    || carType.equals("t5_1_1_1_2")|| carType.equals("t5_1_1_1_2_s")
                                    || carType.equals("t6_1_1_2_2") || carType.equals("t6_1_1_2_2_49")) {
                                key =carType + "_2c";
                            }else if(carType.equals("t5_1_1_3")|| carType.equals("t6_1_2_3") || carType.equals("t6_1_2_3_49") || carType.equals("t6_1_1_1_3")
                                    || carType.equals("t6_1_2_3_s") || carType.equals("t6_1_2_3_s_49")){
                                key =carType + "_3c";
                            }
                        }else if(i==5){//compensation the sixth axis
                            if (carType.equals("t6_1_1_2_2") || carType.equals("t6_1_1_2_2_49")) {
                                key =carType + "_2c";
                            }else if( carType.equals("t6_1_2_3") || carType.equals("t6_1_2_3_49") || carType.equals("t6_1_1_1_3")
                                    || carType.equals("t6_1_2_3_s") || carType.equals("t6_1_2_3_s_49")){
                                key =carType + "_3c";
                            }
                        }
                        if(key.length()>0) {
                            Log.e(TAG,"key:"+key);
                            peakWeight = peakWeight * getRatiosByKey(key);
                        }
                    }
                    if(axisWeights.size()<i+1){
                        axisWeights.add(peakWeight/1000) ;
                    }else{
                        axisWeights.set(i,axisWeights.get(i)+peakWeight/1000);
                    }
                    scaleWeight += peakWeight;
                }
                if (scaleWeight > 0.0) {
                    scaleWeights.add(scaleWeight);
                }
            }
        }

        ConfigTools.writeAxisWeight("当前时间:" + new Date().toLocaleString());
        int axisIndex=1;
        for(double axisWeight :axisWeights){
            ConfigTools.writeAxisWeight("第"+axisIndex+"轴重"+axisWeight+"t");
            axisIndex++;
        }
        if (SharedPreferencesUtil.getScaleMode() == 2) {
            if (scaleWeights.size() == 2) {
                realWeight = scaleWeights.get(0) + scaleWeights.get(1);
            } else if (scaleWeights.size() == 1) {
                realWeight = scaleWeights.get(0) + scaleWeights.get(0);
            } else {
                realWeight = 0.0;
            }
        } else {
            realWeight = 0.0;
            for (int i = 0; i < scaleWeights.size(); i++) {
                realWeight += scaleWeights.get(i);
            }
        }

        LogUtil.e(TAG, "realWeight1:" + realWeight);
        if (realWeight > 0) {
            realWeight = realWeight / 1000;
        }
        if (SharedPreferencesUtil.getScaleMode() == 4) {
            String key;
            switch (InstrumentConfig.bigCarType) {
                case 0:
                    key="big_car_1_weight";
                    break;
                case 1:
                    key="big_car_2_weight";
                    break;
                case 2:
                default:
                   key ="big_car_3_weight";
                    break;
            }
            realWeight = realWeight *getRatiosByKey(key);
        } else {
            String key = carType;
            int scale[];
            if (getAxisNum() == 2) {
                scale = scale_2;
            } else if (getAxisNum() == 3) {
                scale = scale_3;
            } else if (getAxisNum() == 4) {
                scale = scale_4;
            } else if (getAxisNum() == 5) {
                scale = scale_5;
            } else {
                scale = scale_6;
            }
            for (int i = 0; i < scale.length; i++) {
                if (realWeight < scale[i] + 0.0000001) {
                    key = key + "_w_" + i;
                    break;
                }
            }
            if (key.equals(carType)) {
                key = key + "_w_" + 4;
            }
            realWeight = realWeight * getRatiosByKey(key);
        }


        LogUtil.e(TAG, "realWeight2:" + realWeight);
        return realWeight;
    }

    private static double getRatiosByKey(String key){
        return SharedPreferencesUtil.getFactorInt(key, 100)*1.0 / 100;
    }
    private static double getWeightDetect(ScaleDevice device, long innerCode) {
        double weight = 0.00;
        int index = Integer.parseInt(device.getAddress()) - 1;
        double k = ConfigTools.scaleK[index];
        if (k < 0.002) {
            k = 0.002445737515427107;
        }
        try {
            //ConfigTools.writeLog("峰值："+ innerCode);
            weight = innerCode * k;
            //ConfigTools.writeLog("峰值计算重量："+ weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Double.isNaN(weight)) {
            return 0.00;
        }
        return weight;
    }

    public static double getSpeed() {
        int axisnum = getAxisNum();
        double sumSpeed = 0.0;
        for (int i = 0; i < scaleArr.length; i++) {
            double currentSumSpeed = 0.0;
            for (double speed : scaleArr[i].speedArrayList) {
                //ConfigTools.writeLog(i+":speed:"+speed);
                currentSumSpeed += speed;
            }
            if (sumSpeed < currentSumSpeed) {
                sumSpeed = currentSumSpeed;
            }
        }
        ConfigTools.writeLog("sumSpeed:" + sumSpeed + "axisnum:" + axisnum);
        //  Log.e(TAG,"sumSpeed:"+sumSpeed+"count:"+count);
        if (getAxisNum() < 2) {
            return 0.0;
        }
        if (axisnum == 0) {
            axisnum = 1;
        }
        double result = sumSpeed / (axisnum * 1.0);
        if (result > 8.0 || result < 1.0) {
            result = 3.3;
        } else {
            result = result * 1.1;
        }
        return result;
    }

    public static int getAxisNum() {
        int axisNum = 0;
        int index = 0;
        for (int i = 0; i < scaleArr.length; i++) {
            if (axisNum < scaleArr[i].peakArrayList.size() && scaleArr[i].getHeart() > 0) {
                //  Log.e(TAG,"peakArrayList.size"+scaleDevice.peakArrayList.size());
                axisNum = scaleArr[i].peakArrayList.size();
                index = i;
            }
        }
        for (int i = 0; i < scaleArr[index].axisDistanceArrayList.size(); i++) {
            double distance = scaleArr[index].axisDistanceArrayList.get(i);
            Log.e(TAG, "distance:" + distance);
        }
        carTypeName ="";
        carTypeImage=-1;
        if(SharedPreferencesUtil.getScaleMode()==4){
            if(InstrumentConfig.bigCarType==0){
                carTypeName= PortableBalanceApplication.getAppContext().getString(R.string.big_car_1);
                carTypeImage =R.mipmap.big_car_1;
            }else if(InstrumentConfig.bigCarType==1){
                carTypeName= PortableBalanceApplication.getAppContext().getString(R.string.big_car_2);
                carTypeImage =R.mipmap.big_car_2;
            }else{
                carTypeName= PortableBalanceApplication.getAppContext().getString(R.string.big_car_3);
                carTypeImage =R.mipmap.big_car_3;
            }
        }else {
            switch (axisNum) {
                case 0:
                case 1:
                    carType = "t1";
                    carTypeName="";
                    carTypeImage =R.mipmap.t2_1_1;
                    break;
                case 2:
                    carType = "t2_1_1";
                    carTypeName="11";
                    carTypeImage =R.mipmap.t2_1_1;
                    break;
                case 3:
                    if (scaleArr[index].axisDistanceArrayList.get(0) < 2.2) {
                        carType = "t3_2_1";//0A,12,2
                        carTypeName="21";
                        carTypeImage =R.mipmap.t3_2_1;
                    } else if (scaleArr[index].axisDistanceArrayList.get(1) < 1.6) {
                        carType = "t3_1_2";
                        carTypeName="12+";
                        carTypeImage =R.mipmap.t3_1_2;
                    } else {
                        carType = "t3_1_1_1";
                        carTypeName="111";
                        carTypeImage =R.mipmap.t3_1_1_1_0;
                    }
                    break;
                case 4:
                    if (scaleArr[index].axisDistanceArrayList.get(2) < 1.6) {
                        if (scaleArr[index].axisDistanceArrayList.get(0) < 2.2) {
                            carType = "t4_1_1_2_0";
                            carTypeName="112+";
                            carTypeImage =R.mipmap.t4_1_1_2_0;
                        } else {
                            carType = "t4_1_1_2_s";
                            carTypeName="112A";
                            carTypeImage =R.mipmap.t4_1_1_2_s;
                        }

                    } else if (scaleArr[index].axisDistanceArrayList.get(1) < 1.6) {
                        carType = "t4_1_2_1";
                        carTypeName="121A+";
                        carTypeImage =R.mipmap.t4_1_2_1;
                    } else {
                        carType = "t4_1_1_1_1";
                        carTypeName="1111A";
                        carTypeImage =R.mipmap.t4_1_1_1_1;
                    }
                    break;
                case 5:
                    if (scaleArr[index].axisDistanceArrayList.get(2) < 1.6 && scaleArr[index].axisDistanceArrayList.get(3) < 1.6) {
                        carType = "t5_1_1_3";
                        carTypeName="113";
                        carTypeImage =R.mipmap.t5_1_1_3;
                    } else if (scaleArr[index].axisDistanceArrayList.get(0) < 3 && scaleArr[index].axisDistanceArrayList.get(0) > 1.8 && scaleArr[index].axisDistanceArrayList.get(3) < 1.6) {
                        carType = "t5_1_1_1_2";
                        carTypeName="1112";
                        carTypeImage =R.mipmap.t5_1_1_1_2;
                    } else if (scaleArr[index].axisDistanceArrayList.get(1) < 1.6 && scaleArr[index].axisDistanceArrayList.get(3) < 1.6) {
                        carType = "t5_1_2_2";
                        carTypeName="122+";
                        carTypeImage =R.mipmap.t5_1_2_2;
                    } else if (scaleArr[index].axisDistanceArrayList.get(1) < 1.6) {
                        carType = "t5_1_2_1_1";
                        carTypeName="1211A+";
                        carTypeImage =R.mipmap.t5_1_2_1_1;
                    } else {
                        carType = "t5_1_1_1_1_1";
                        carTypeName="11111A";
                        carTypeImage =R.mipmap.t5_1_1_1_1_1;
                    }
                    break;
                case 6:
                    if (scaleArr[index].axisDistanceArrayList.get(4) < 1.6) {
                        if(scaleArr[index].axisDistanceArrayList.get(3) < 1.6) {
                            if (scaleArr[index].axisDistanceArrayList.get(1) < 1.6) {
                                carType = "t6_1_2_3";
                                carTypeName = "123";
                                carTypeImage = R.mipmap.t6_1_2_3;
                            } else {
                                carType = "t6_1_1_1_3";
                                carTypeName = "1113";
                                carTypeImage = R.mipmap.t6_1_1_1_3;
                            }
                        }else {
                            carType = "t6_1_1_2_2";
                            carTypeName = "1122A";
                            carTypeImage = R.mipmap.t6_1_1_2_2;
                        }
                    } else if (scaleArr[index].axisDistanceArrayList.get(2) < 1.6 && scaleArr[index].axisDistanceArrayList.get(4) < 1.6) {

                    } else {
                        carType = "t6_1_1_2_1_1";
                    }
            }
        }
        Log.e(TAG, carType);
        return axisNum;
    }


    public static void setInnerCode(String command) {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getAddress().equals(command.substring(2, 4))) {
                scaleDevice.setHeart(5);
                //  h a c l 55 adr F3 06 dd dd dd dd dd dd ** 5A
//                String buffer = "静态称重上报" + command + ConfigTools.LINE_BREAK + "帧头：" +
//                        command.substring(0, 2) +
//                        ConfigTools.LINE_BREAK +
//                        "地址：" +
//                        command.substring(2, 4) +
//                        ConfigTools.LINE_BREAK +
//                        "命令：" +
//                        command.substring(4, 6) +
//                        ConfigTools.LINE_BREAK +
//                        "长度：" +
//                        command.substring(6, 8) +
//                        ConfigTools.LINE_BREAK +
//                        "稱重数据：" +
//                        command.substring(8, 16) +
//                        ConfigTools.LINE_BREAK +
//                        "計數数据：" + command.substring(16, 20) + ConfigTools.LINE_BREAK +
//                        "校驗和：" + command.substring(20, 22) + ConfigTools.LINE_BREAK +
//                        "帧尾：" + command.substring(22) + ConfigTools.LINE_BREAK +
//                        "有效数据：" + command.substring(8, 14) + ConfigTools.LINE_BREAK +
//                        "有效数据转10进制：" + Long.parseLong(command.substring(8, 14), 16) + ConfigTools.LINE_BREAK +
//                        "动态零位：" +
//                        scaleDevice.getZeroInnerCode() +
//                        ConfigTools.LINE_BREAK;
                //  ConfigTools.writeStaticWeightLog(buffer);
                long value = Long.parseLong(command.substring(8, 14), 16);
                if (scaleDevice.getIndex() < 15) {
                    scaleDevice.setIndex(scaleDevice.getIndex() + 1);
                } else {
                    scaleDevice.setIndex(11);
                }
                if (scaleDevice.getZeroInnerCode() == 0 && scaleDevice.getIndex() < 11) {
                    scaleDevice.setZeroInnerCode(value);
                }
                scaleDevice.setInnerCode(value);
                break;
            }
        }
    }

    public static ScaleDevice getScaleByAddress(String address) {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (address.equals(scaleDevice.getAddress())) {
                return scaleDevice;
            }
        }
        return null;
    }

    public static Boolean isScaleConnected() {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getHeart() > 0) {
                return true;
            }
        }
        return false;
    }

    public static String closeCommand(String address) {
        String command = "F1";
        return FRAME_HEADER + address + command + CLOSE.replace(" ", "") + SocketUtil.checkSum(address, command, CLOSE) + FRAME_TAIL;
    }

    public static String closeScaleCommand(String address) {
        String command = "F1";
        return FRAME_HEADER + address + command + CLOSE_SCALE.replace(" ", "") + SocketUtil.checkSum(address, command, CLOSE_SCALE) + FRAME_TAIL;
    }

    public static String keepOpenCommand(String address) {
        String command = "F1";
        // Log.e("keepOpenCommand", address);
        return FRAME_HEADER + address + command + KEEP_OPEN.replace(" ", "") + SocketUtil.checkSum(address, command, KEEP_OPEN) + FRAME_TAIL;
    }

    public static String keepScaleOpenCommand(String address) {
        String command = "F1";
        String dataLength = "06";
        Log.e("keepScaleOpenCommand", address);
        return FRAME_HEADER + address + command + dataLength + KEEP_OPEN.replace(" ", "") + SocketUtil.checkSum(address, command, "06 " + KEEP_OPEN) + FRAME_TAIL;
    }

    public static void updateScaleHeart() {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getHeart() > 0) {
                scaleDevice.setHeart(scaleDevice.getHeart() - 1);
            }
        }
    }

    public static boolean allResultBack() {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getHeart() > 0 && !resultAddressList.contains(scaleDevice.getAddress())) {
                return false;
            }
        }
        return true;

    }

    public static void setScaleConnectStatus(String address) {
        // LogUtil.v("setScaleConnectStatus",address);
        for (int i = 0; i < SharedPreferencesUtil.getScaleMode(); i++) {
            if (scaleArr[i].getAddress().equals("00")) {
                scaleArr[i].setAddress(address);
                scaleArr[i].setIndex(i);
                scaleArr[i].setHeart(5);
                //LogUtil.v("setScaleConnectStatus", scaleArr[i].getAddress() + scaleArr[i].getHeart());
                return;
            } else {
                if (scaleArr[i].getAddress().equals(address)) {
                    scaleArr[i].setHeart(5);
                    //  LogUtil.v("setScaleConnectStatus", " exist" + scaleArr[i].getAddress() + scaleArr[i].getHeart());
                    return;
                }
            }
        }
    }


    public static void updateScaleBattery(String address, String batteryData) {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getAddress().equals(address)) {
                Log.e(TAG,scaleDevice.getName());
                scaleDevice.setBattery(getBatteryResult(batteryData));
                return;
            }
        }
    }

    private static double getBatteryResult(String batteryData) {
        // Log.d("battery", ":"+batteryData);
        double battery = Integer.parseInt(batteryData, 16) * 3.28 * 2 / 255;
         Log.d("battery2", "==:"+battery);
        battery = (battery - 3.6) / (4.3 - 3.6) * 100;
        // Log.d("battery2","==:"+battery);
        return battery;
    }

    public static void updateInstrumentBattery(String batteryData) {
        int temp = Integer.parseInt(batteryData, 16);
        String bat = Integer.toHexString(temp);

        instrumentDevice.setBattery(getInstrumentBatteryResult(bat));
    }

    private static double getInstrumentBatteryResult(String batteryData) {
         Log.d("battery", ":"+batteryData);

        double battery = Integer.parseInt(batteryData, 16) * 7.2 * 3.3*3.51 / 255;
        ConfigTools.writeBatteryLog(new Date().toLocaleString() +"仪表电量电压:"+battery);
         Log.d("battery1", "==:"+battery);
        battery = (battery - 7.2) / (8.4- 7.2) * 100;
        // Log.d("battery2","==:"+battery);
        return battery;
    }
//    public static void updateInstrumentSignal(String address, String signal) {
//        double signalStrength = getSingalStrength(signal);
//        Log.d("signal Instrument", address + ":" + signalStrength);
//        //setScaleConnectStatus(address);
//        InstrumentConfig.instrumentDevice.setSignal(signalStrength);
//    }


//    public static void updateScaleSingal(String address, String signal) {
//        double signalStrength = getSingalStrength(signal);
//        Log.d("signal", address + ":" + signalStrength);
//        for (ScaleDevice scaleDevice : scaleArr) {
//            if (scaleDevice.getAddress().equals(address)) {
//               // scaleDevice.setSignal(signalStrength);
//                return;
//            }
//        }
//    }

    public static void updateDetectInfo(String command) {
        Log.e("updateDetectInfo", command);
        ConfigTools.writeLog("原始数据:" + command);
        int axisNum = 0;
        StringBuilder peakData = new StringBuilder();
        StringBuilder timeData = new StringBuilder();
        Vector<Integer> errorPeaks = new Vector<>();
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getAddress().equals(command.substring(2, 4))) {
//                if(scaleDevice.peakArrayList.size()%6!=0){
//                    return;
//                }
                Log.e(TAG, "scaleDevice.getLastSixCommand():" + scaleDevice.getLastSixCommand());
                Log.e(TAG, "command:" + command);
                if (scaleDevice.getLastSixCommand().equals(command)) {
                    return;
                } else {
                    scaleDevice.setLastSixCommand(command);
                }
                String datalength = command.substring(6, 8);
                int len = Integer.parseInt(datalength, 16);
                axisNum = (len - 2) / 8;
                if (scaleDevice.getAddress().equals("04")) {
                    scaleDevice.setAxiNum(scaleDevice.getAxiNum() + axisNum);
                }
                LogUtil.e(TAG, "axisNum:" + axisNum);
                scaleDevice.setAxiNum(axisNum);//9---4-2-2
                String tempString = command.substring(8);
                int peaksLength = axisNum * 8;
                String peaks = tempString.substring(0, peaksLength);
                tempString = tempString.substring(peaksLength);
                //96 -8-4 =84= 42 = 20,22
                // LogUtil.e(TAG,"peaks:"+peaks+"tempString:" +tempString);
                int timesLength = (2 * axisNum + 1) * 4;
                String times = tempString.substring(0, timesLength);
                // LogUtil.e(TAG,"times"+times);
                for (int i = 0; i < peaks.length(); i = i + 8) {
                    String peak = peaks.substring(i, i + 8);
                    //Log.e(TAG,"peakLong"+peakLong);67000
                    //scaleDevice.peakArrayList.add(peakLong);
                    peakData.append(peak.substring(0, 2) + " " + peak.substring(2, 4) + " " + peak.substring(4, 6) + " " + peak.substring(6, 8) + "\r\n");
                    Long peakLong = Long.parseLong(peak.substring(0, 6), 16);
                    scaleDevice.peakArrayList.add(peakLong);
                }
                Integer timeIndex = 0;
                //18--4
                scaleDevice.timeArrayList.clear();

                int timeCount = 1;
                for (int i = 0; i < times.length(); i = i + 4) {
                    timeData.append(times.substring(i, i + 2));
                    timeData.append(" ");
                    timeData.append(times.substring(i + 2, i + 4));
                    if (timeCount % 2 == 0) {
                        timeData.append(" *");
                    }
                    timeData.append(ConfigTools.LINE_BREAK);
                    timeCount++;
                    if (!errorPeaks.contains(timeIndex)) {
                        String time = times.substring(i, i + 4);
                        int timeInt = Integer.parseInt(time, 16);
                        scaleDevice.timeArrayList.add(timeInt);
                    }
                    //   Log.e(TAG,"time"+timeInt);
                }

                // 06 B7 3A 00- 07 D3 F2 00-0072-0027-0054-002A-0252
                double scaleWidht = 0.29;
//                if (SharedPreferencesUtil.getInt("weighWidth", 0) == 1) {
//                    scaleWidht = 0.33;
//                }
                //2n+1 time : 3(1),5(1,3),7(1,3,5),9,11,13
                //int count = 1;
                for (int i = 1; i < scaleDevice.timeArrayList.size(); i = i + 2) {//2*j+1,1-3-5-7
                    int peakTime = scaleDevice.timeArrayList.get(i);
                    if (!errorPeaks.contains(timeIndex)) {
                        // Log.e("updateDetectInfo", "=======axisTime:" + peadkTime + "ms" + ":" + i);
                        //Double speed = scaleWidht / 1000 * 3600 * 1000 / axisTime;
                        Double speed = 3.0;
                        if (peakTime == 0) {
                            ConfigTools.writeLog("存在零时间");
                        } else {
                            speed = scaleWidht * 3600 / peakTime / 10;//d2 = senorlength / (AThroughTime.get(j * 2 + 1) * 10.0D) * 3.6D;
                        }
                        scaleDevice.speedArrayList.add(speed);
                        if (i + 1 < scaleDevice.timeArrayList.get(i)) {
                            int axisTime = scaleDevice.timeArrayList.get(i + 1);
                            double distance = speed * axisTime * 10 / 3600;
                            scaleDevice.axisDistanceArrayList.add(distance);
                            ConfigTools.writeLog("distance" + distance);
                        }
                    }
                }
                break;
            }
        }

//        StringBuilder stringBuffer = new StringBuilder("桢头：55"+ConfigTools.LINE_BREAK);
//        stringBuffer.append("地址：");
//        stringBuffer.append(command.substring(2,4));
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("命令：");
//        stringBuffer.append("F3");
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("数据长度：");
//        stringBuffer.append(command.substring(6,8));
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("峰值数据：");
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append(peakData.toString());
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("时间数据：");
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append(timeData.toString());
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("校验和：");
//        stringBuffer.append(command.substring(command.length()-4,command.length()-2));
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        stringBuffer.append("桢尾：");
//        stringBuffer.append(command.substring(command.length()-2));
//        stringBuffer.append(ConfigTools.LINE_BREAK);
//        ConfigTools.writeLog("分割数据:"+stringBuffer);
    }

    //06B73A0007D3F200007200270054002A0252

    private static double getSingalStrength(String signal) {
        return Integer.parseInt(signal, 16) * 1.0 / 255 * 100;
    }

    public static String getCarType() {
        if (getAxisNum() < 2) {
            return "未识别车型";
        } else if (getAxisNum() == 2) {
            return "二轴载货汽车";
        } else if (getAxisNum() == 3) {
            for (ScaleDevice scaleDevice : scaleArr) {
                if (scaleDevice.getAxiNum() == 3) {
                    // Log.e(TAG,"distance"+scaleDevice);
//                  for(double distance :scaleDevice.axisDistanceArrayList){
//                      if(distance>16){
//                          return ;
//                      }
//                  }
                    break;
                }
            }
        }
        return "";
    }


    public static void initScaleDevices(int mode) {
        ConfigTools.buildSCaleKR();
        for (int i = 0; i < 4; i++) {
            staticValueListForScaleArr[i].adddress = FOUR_SCALE_ADDRESS_ARR[i];
            if (mode == 4) {
                scaleArr[i].setAddress(FOUR_SCALE_ADDRESS_ARR[i]);
            } else {
                scaleArr[i].setAddress("");
            }
        }
    }

    //112 -8+48
    public static ArrayList<Double> getValuesbyAddress(String address, boolean isStatic) {
        for (StaticValueListForScale value : staticValueListForScaleArr) {
            if (address.equals(value.adddress)) {
                if (isStatic) {
                    return value.staticValueList;
                } else {
                    return value.loadValueList;
                }
            }
        }
        return null;
    }

    public static String getSingalAddress(String address) {
        for (int i = 0; i < ADDRESS_ARR.length; i++) {
            if (ADDRESS_ARR[i].equals(address)) {
                return SIGNAL_ARR[i];
            }
        }
        return "01";
    }


    public static String getAddressByHeart(String heart_address) {
        for (int i = 0; i < InstrumentConfig.HEART_BEATS.length; i++) {
            if (heart_address.equals(InstrumentConfig.HEART_BEATS[i])) {
                return InstrumentConfig.ADDRESS_ARR[i];
            }
        }
        return "";
    }

    public static boolean noScaleOnline() {
        for (ScaleDevice scaleDevice : scaleArr) {
            if (scaleDevice.getHeart() > 0 && scaleDevice.getAddress().length() > 0) {
                return false;
            }
        }
        return true;
    }
}

