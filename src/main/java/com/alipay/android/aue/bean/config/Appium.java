package com.alipay.android.aue.bean.config;

/**
 * Created by bingo on 2016/10/9.
 */
public class Appium {
    private String appPath;
    private String host;
    private String port;
    private String platformName;
    private int interval;
    private String serialsNumber;
    private String processName;

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getSerialsNumber() {
        return serialsNumber;
    }

    public void setSerialsNumber(String serialsNumber) {
        this.serialsNumber = serialsNumber;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
