package com.lu.portable.detect;

public class AlxenumrexDetail {
    public double pos_weight;
    public double pre_weight;
    public double speed;

    public AlxenumrexDetail() {
        pre_weight = 0.0D;
        pos_weight = 0.0D;
        speed = 0.0D;
    }

    public AlxenumrexDetail(double pre_weight, double pos_weight, double speed) {
        this.pre_weight = pre_weight;
        this.pos_weight = pos_weight;
        this.speed = speed;
    }

    public void clear_data() {
        pre_weight = 0.0D;
        pos_weight = 0.0D;
        speed = 0.0D;
    }

    public void doubleWeight() {
        pre_weight *= 2.0D;
        pos_weight *= 2.0D;
    }
}