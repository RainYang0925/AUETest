package com.alipay.android.aue.bean;

import java.io.Serializable;

/**
 * Created by bingo on 2016/10/8.
 */
public class CPUBean implements Serializable{
    private String processName;
    private float usagePercent;
    private String time;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public float getUsagePercent() {
        return usagePercent;
    }

    public void setUsagePercent(float usagePercent) {
        this.usagePercent = usagePercent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{time:" + getTime() + ",processName:" + getProcessName() + ",usage:" + getUsagePercent() + "}";
    }
}
