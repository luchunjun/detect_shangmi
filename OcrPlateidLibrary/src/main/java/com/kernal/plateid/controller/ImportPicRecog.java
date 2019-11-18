package com.kernal.plateid.controller;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.kernal.plateid.PlateCfgParameter;
import com.kernal.plateid.PlateIDAPI;
import com.kernal.plateid.PlateRecognitionParameter;
import com.kernal.plateid.RecogService;
import com.kernal.plateid.CoreSetup;
import com.kernal.plateid.TH_PlateIDCfg;
import com.kernal.plateid.TH_PlateIDResult;

import java.io.File;

/***
 * @author user
 * 导入图像识别类
 */
public class ImportPicRecog {
    private byte[] data = null;
    private Context context;
    private CoreSetup coreSetup;
    private int iInitPlateIDSDK;
    private RecogService.MyBinder recogBinder;
    private String[] recogResult = new String[14];
    private PlateRecognitionParameter prp = new PlateRecognitionParameter();
    private Point picPoint = new Point();
    private int nRet;
    private PlateIDAPI plateIDAPI;
    public ImportPicRecog(Context context) {
        nRet = -2;
        iInitPlateIDSDK = -1;
        plateIDAPI = new PlateIDAPI();
        if(recogBinder == null){
            coreSetup = new CoreSetup();
            this.context = context;
            //快速、导入、拍照识别模式参数(0:快速、导入、拍照识别模式-----2:精准识别模式)
            RecogService.recogModel = 0;
            //启动识别服务
            Intent recogIntent = new Intent(context,
                    RecogService.class);
            context.bindService(recogIntent, recogConn,
                    Service.BIND_AUTO_CREATE);
        }
    }

    /**
     * 识别获取结果
     * @param recogPicPath 传入识别图像的路径
     * @return 识别结果
     */
    public String[] recogPicResults(String recogPicPath) {
        if(recogBinder != null){
            obtainPicSize(recogPicPath);
            // 图像高度
            prp.height = picPoint.y;
            // 图像宽度
            prp.width = picPoint.x;
            // 图像文件
            prp.pic = recogPicPath;
            recogResult = recogBinder.doRecogDetail(prp);
            nRet = recogBinder.getnRet();
            if (nRet != 0) {
                recogResult[0] = "错误码:"+String.valueOf(nRet)+"，请查阅开发手册寻找解决方案";
            }
            releaseService();
        }
        return recogResult;

    }
    private String [] trd(){
        String [] fieldvalue = recogPlate(prp.picByte, prp.pic, prp.width, prp.height, prp.userData, prp.plateIDCfg);
        if (fieldvalue[0] != null) {
            Log.d("RecogService", "识别结果数组说明:\n0:车牌号\n1:车牌颜色\n2:车牌颜色代码\n3:车牌类型代码\n4:识别结果车牌号可信度\n5:亮度评价\n6:车牌运动方向\n7:车牌左上点横坐标\n8:车牌左上点纵坐标\n9:车牌右下点横坐标\n10:车牌右下点纵坐标（7.8.9.10是车牌区域范围）\n11:时间\n12:车身亮度\n13:车身颜色 ");
        }
        return fieldvalue;
    }
    private synchronized String[] recogPlate(byte[] picByte, String pic, int width, int height, String userData, TH_PlateIDCfg plateIDCfg) {
        int[] nResultNums = new int[]{10};
        int[] nRets = new int[]{-1};
        TH_PlateIDResult plateidresult = new TH_PlateIDResult();
        TH_PlateIDResult[] plateidresults;
        if (picByte != null && picByte.length > 0) {
            plateidresults = this.plateIDAPI.TH_RecogImageByte(picByte, width, height, plateidresult, nResultNums, plateIDCfg.left, plateIDCfg.top, plateIDCfg.right, plateIDCfg.bottom, nRets, plateIDCfg.bRotate, plateIDCfg.scale);
        } else {
            plateidresults = this.plateIDAPI.TH_RecogImage(pic, width, height, plateidresult, nResultNums, plateIDCfg.left, plateIDCfg.top, plateIDCfg.right, plateIDCfg.bottom, nRets);
        }

        this.nRet = nRets[0];
        String[] tempFieldvalue = new String[15];
        if (nRets[0] != 0) {
            tempFieldvalue[14] = userData;
        } else {
            tempFieldvalue[14] = userData;
            for(int i = 0; i < nResultNums[0]; ++i) {
                if (plateidresults != null && plateidresults[i] != null) {
                    if (i == 0) {
                        tempFieldvalue[0] = plateidresults[i].license;
                        tempFieldvalue[1] = plateidresults[i].color;
                        tempFieldvalue[2] = this.int2string(plateidresults[i].nColor);
                        tempFieldvalue[3] = this.int2string(plateidresults[i].nType);
                        tempFieldvalue[4] = this.int2string(plateidresults[i].nConfidence);
                        tempFieldvalue[5] = this.int2string(plateidresults[i].nBright);
                        tempFieldvalue[6] = this.int2string(plateidresults[i].nDirection);
                        tempFieldvalue[7] = this.int2string(plateidresults[i].left);
                        tempFieldvalue[8] = this.int2string(plateidresults[i].top);
                        tempFieldvalue[9] = this.int2string(plateidresults[i].right);
                        tempFieldvalue[10] = this.int2string(plateidresults[i].bottom);
                        tempFieldvalue[11] = this.int2string(plateidresults[i].nTime);
                        tempFieldvalue[12] = this.int2string(plateidresults[i].nCarBright);
                        tempFieldvalue[13] = this.int2string(plateidresults[i].nCarColor);
                        tempFieldvalue[14] = userData;
                        data = plateidresults[i].pbyBits;
                    } else {
                        tempFieldvalue[0] = tempFieldvalue[0] + ";" + plateidresults[i].license;
                        tempFieldvalue[1] = tempFieldvalue[1] + ";" + plateidresults[i].color;
                        tempFieldvalue[2] = tempFieldvalue[2] + ";" + this.int2string(plateidresults[i].nColor);
                        tempFieldvalue[3] = tempFieldvalue[3] + ";" + this.int2string(plateidresults[i].nType);
                        tempFieldvalue[4] = tempFieldvalue[4] + ";" + this.int2string(plateidresults[i].nConfidence);
                        tempFieldvalue[5] = tempFieldvalue[5] + ";" + this.int2string(plateidresults[i].nBright);
                        tempFieldvalue[6] = tempFieldvalue[6] + ";" + this.int2string(plateidresults[i].nDirection);
                        tempFieldvalue[7] = tempFieldvalue[7] + ";" + this.int2string(plateidresults[i].left);
                        tempFieldvalue[8] = tempFieldvalue[8] + ";" + this.int2string(plateidresults[i].top);
                        tempFieldvalue[9] = tempFieldvalue[9] + ";" + this.int2string(plateidresults[i].right);
                        tempFieldvalue[10] = tempFieldvalue[10] + ";" + this.int2string(plateidresults[i].bottom);
                        tempFieldvalue[11] = tempFieldvalue[11] + ";" + this.int2string(plateidresults[i].nTime);
                        tempFieldvalue[12] = tempFieldvalue[12] + ";" + this.int2string(plateidresults[i].nCarBright);
                        tempFieldvalue[13] = tempFieldvalue[13] + ";" + this.int2string(plateidresults[i].nCarColor);
                    }
                }
            }
        }
        return tempFieldvalue;
    }
    /***
     * 释放核心
     * 连续识别不需要重复初始化、释放
     */
    private void releaseService(){
        if(recogBinder != null){
            recogBinder.UninitPlateIDSDK();
            context.unbindService(recogConn);
        }
    }
    private String int2string(int i) {
        String str = "";

        try {
            str = String.valueOf(i);
        } catch (Exception var4) {
        }

        return str;
    }
    private ServiceConnection recogConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            recogConn = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            recogBinder = (RecogService.MyBinder) service;
            setPlateid();
        }
    };

    /***
     * 初始化设置识别车牌核心
     *
     */
    private void setPlateid() {
        iInitPlateIDSDK = recogBinder.getInitPlateIDSDK();
        if (iInitPlateIDSDK != 0) {
            Toast.makeText(context, "错误码：" + iInitPlateIDSDK, Toast.LENGTH_LONG).show();
        } else {
            // 导入图片方式参数
            int imageformat = 0;
            PlateCfgParameter cfgparameter = new PlateCfgParameter();
            //识别阈值(取值范围0-9, 0:最宽松的阈值, 9:最严格的阈值, 5:默认阈值)
            cfgparameter.nOCR_Th = coreSetup.nOCR_Th;
            // 定位阈值(取值范围0-9, 0:最宽松的阈值9, :最严格的阈值, 5:默认阈值)
            cfgparameter.nPlateLocate_Th = coreSetup.nPlateLocate_Th;
            // 省份顺序,例:cfgparameter.szProvince = "京津沪";最好设置三个以内，最多五个。
            cfgparameter.szProvince = coreSetup.szProvince;

            // 是否开启个性化车牌:0开启；1关闭
            cfgparameter.individual = coreSetup.individual;
            // 双层黄色车牌是否开启:开启；3关闭
            cfgparameter.tworowyellow = coreSetup.tworowyellow;
            // 单层武警车牌是否开启:4开启；5关闭
            cfgparameter.armpolice = coreSetup.armpolice;
            // 双层军队车牌是否开启:6开启；7关闭
            cfgparameter.tworowarmy = coreSetup.tworowarmy;
            // 农用车车牌是否开启:8开启；9关闭
            cfgparameter.tractor = coreSetup.tractor;
            // 使馆车牌是否开启:12开启；13关闭
            cfgparameter.embassy = coreSetup.embassy;
            // 双层武警车牌是否开启:16开启；17关闭
            cfgparameter.armpolice2 = coreSetup.armpolice2;
            //厂内车牌是否开启     18:开启  19关闭
//            cfgparameter.Infactory = coreSetup.Infactory;
            //民航车牌是否开启  20开启 21 关闭
//            cfgparameter.civilAviation = coreSetup.civilAviation;
            //领事馆车牌开启   22开启   23关闭
            cfgparameter.consulate = coreSetup.consulate;
            //新能源车牌开启  24开启  25关闭
            cfgparameter.newEnergy = coreSetup.newEnergy;

            recogBinder.setRecogArgu(cfgparameter, imageformat);
            // 图像高度
            prp.devCode = coreSetup.Devcode;
        }
    }

    /***
     * 动态获取图像的宽高
     * @param recogPicPath 识别图像路径
     */
    private void obtainPicSize(String recogPicPath) {
        File file = new File(recogPicPath);
        BitmapFactory.Options options = null;
        if (file.exists()) {
            try {
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(recogPicPath, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //目前最大支持到4096*4096的图片
            if (options != null) {
                int picMaxWH = 4096;
                if (options.outWidth <= picMaxWH && options.outHeight <= picMaxWH) {
                    picPoint.set(options.outWidth,options.outHeight);
                } else {
                    Toast.makeText(context, "读取文件错误，图片超出识别限制4096*4096",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(context, "读取文件不存在",
                    Toast.LENGTH_LONG).show();
        }
    }
}
