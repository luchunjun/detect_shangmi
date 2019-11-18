package com.lu.portable.detect.model;

public class CommonDevice {
    private String TAG = "CommonDevice";
    private String address = "";
    private int heart = 0;
    private double battery = 80.0;
    private double signal = 100.0;

    public CommonDevice(){
    }
    public CommonDevice(String address){
        address =address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        if (address.length() == 0) {
            return "00";
        }
        return address;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }
    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

}
