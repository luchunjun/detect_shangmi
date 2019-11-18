package com.lu.portable.detect.model;

import java.util.HashMap;
import java.util.Iterator;

public class Device {
    public int elec = -1;
    public HashMap<String, Device> refs = new HashMap<>();
    public int signal = 31;
    public int status = 505;

    public boolean updateElec(int elec) {
        if (status == 504 || status != 503) {
            return false;
        } else {
            this.elec = elec;
            return true;
        }
    }

    public boolean updateSignal(int signalCode) {
        if ((504 == this.status) || (signalCode == this.signal)) {
            return false;
        }
        switch (signalCode) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 16:
                break;
            case 31:
                break;
            default:
                break;
        }
//        while (true) {
//            this.signal = signalCode;
//           // return true;
//            this.status = 503;
//            continue;
//            this.status = 505;
//            Iterator localIterator = refs.keySet().iterator();
//            while (localIterator.hasNext()) {
//                String str = (String) localIterator.next();
//                ((Device) refs.get(str)).updateStatus(this.status);
//            }
//        }
        return true;
    }

    public boolean updateStatus(int statusCode) {
        if (statusCode == this.status) {
            return false;
        }
        while (statusCode != 507 && this.status == 504);
        int i = statusCode;
        switch (statusCode) {
            default:
            case 503:
            case 507:
                for (i = statusCode; ; i = 505) {
                    this.status = i;
                    return true;
                }
            case 500:
            case 501:
            case 502:
            case 504:
            case 505:
            case 506:
        }
        this.signal = 31;
        Iterator localIterator = this.refs.keySet().iterator();
        while (true) {
            if (!localIterator.hasNext())
                break;
            String key = (String) localIterator.next();
            refs.get(key).updateStatus(statusCode);
        }
        return  false;
    }
}