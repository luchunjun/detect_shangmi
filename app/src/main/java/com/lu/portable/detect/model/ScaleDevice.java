package com.lu.portable.detect.model;

import android.util.Log;

import com.lu.portable.detect.configtools.ConfigTools;

import java.util.ArrayList;

public class ScaleDevice extends CommonDevice {
    private String TAG = "ScaleDevice";

    public String getLastSixCommand() {
        return lastSixCommand;
    }

    public void setLastSixCommand(String lastSixCommand) {
        this.lastSixCommand = lastSixCommand;
    }

    private String lastSixCommand="";
    public ArrayList<Long> peakArrayList = new ArrayList<>();
    public ArrayList<Integer> timeArrayList = new ArrayList<>();
    public ArrayList<Double> speedArrayList = new ArrayList<>();
    public ArrayList<Double> axisDistanceArrayList = new ArrayList<>();
    public int getAxiNum() {
        return axiNum;
    }

    public void setAxiNum(int axiNum) {
        this.axiNum = axiNum;
    }

    public int getBatteryFlag() {
        return batteryFlag;
    }

    public void setBatteryFlag(int batteryFlag) {
        this.batteryFlag = batteryFlag;
    }

    private int batteryFlag = 10;
    private int axiNum = 0;
    private Long zeroInnerCode = 0L;

    public Long getZeroInnerCode() {
        return zeroInnerCode;
    }

    public void setZeroInnerCode(Long zeroInnerCode) {
        Log.e("ScaleDevice","setZeroInnerCode:"+zeroInnerCode);
        this.zeroInnerCode = zeroInnerCode;
    }

    private int index = 0;
    private Long innerCode = 0L;


    public long getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(Long innerCode) {
        this.innerCode = innerCode;
    }

    public String getName() {
        return getScaleName(getAddress());
    }

    private String getScaleName(String address) {
        switch (address) {
            case "01":
                return "A";
            case "02":
                return "B";
            case "03":
                return "C";
            case "04":
                return "D";
            case "05":
                return "C";
            case "06":
                return "D";
            default:
                return "";
        }
    }

    //    private String name = "";
    private int getResult(int value) {
        return value & 512;
    }

    public String getWeight() {
        int index = Integer.parseInt(getAddress());
        double k = ConfigTools.scaleK[index-1];
        double innerCode = getInnerCode() / 1000.0D * 1000 + getResult((int) getInnerCode() % 1000);
        try {
            //Log.e(TAG, "k:" + k +":"+getZeroInnerCode()) ;
            weight = (innerCode -getZeroInnerCode()) * k ;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        if (Double.isNaN(weight)) {
            return "0.00";
        }
        return String.format("%.2f", weight);
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    private double weight = 0.00;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
