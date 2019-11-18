package com.lu.portable.detect.model;

public class RunStatus {
    private static RunStatus _instance = new RunStatus();
    public String status = "ready";

    public static RunStatus Initialize() {
        return _instance;
    }

    public boolean updateRunstatus(String runStatus) {
        if (runStatus.equals(status)) {
            return false;
        } else {
            this.status = runStatus;
            return true;
        }
    }
}