package com.lu.portable.detect.model;

public class LoopDetector extends Device
{
    public boolean hasCar;
    public boolean updateCheckStatus(boolean hasCarFlag)
    {
        if (hasCar == hasCarFlag){
            return false;
        }else {
            this.hasCar = hasCarFlag;
            return true;
        }
    }
}