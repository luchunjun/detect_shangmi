package com.lu.portable.detect.configtools.model;

public class StaticValue implements Comparable<StaticValue> {
    private double loadValue;
    private String sensor;
    private double staticValue;

    public StaticValue(double loadValue, double staticValue, String sensor) {
        this.loadValue = loadValue;
        this.staticValue = staticValue;
        this.sensor = sensor;
    }

    public int compareTo(StaticValue paramStaticValue) {
        return new Double(this.loadValue).compareTo(new Double(paramStaticValue.getLoadValue()));
    }

    public double getLoadValue() {

        return loadValue;
    }

    public void setLoadValue(double loadValue) {

        this.loadValue = loadValue;
    }

    public String getSensor() {
        return sensor;
    }

    public double getStaticValue() {
        return staticValue;
    }

    public void setStaticValue(double staticValue) {
        this.staticValue = staticValue;
    }

    public int is_equeal(double loadValue) {
        return new Double(this.loadValue).compareTo(new Double(loadValue));
    }
}