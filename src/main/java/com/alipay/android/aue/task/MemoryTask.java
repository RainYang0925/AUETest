package com.alipay.android.aue.task;

/**
 * Created by bingo on 2016/10/1.
 */
public class MemoryTask implements Runnable{
    private final static String commandFormat = "adb -s %s shell dumpsys meminfo %s";

    //device serious number
    private String sn;

    //record precess name
    private String processName;

    public MemoryTask() {
    }

    public void run() {
        String command = String.format(commandFormat, sn, processName);



    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
