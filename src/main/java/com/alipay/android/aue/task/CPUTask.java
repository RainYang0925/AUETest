package com.alipay.android.aue.task;

import com.alipay.android.aue.bean.CPUBean;
import com.alipay.android.aue.manager.FileManager;
import com.alipay.android.aue.utils.AppiumUtils;
import com.alipay.android.aue.utils.CommonUtils;
import org.apache.log4j.Logger;

/**
 * Created by bingo on 2016/10/1.
 */
public class CPUTask implements Runnable{
    private final static String commandFormat = "adb -s %s shell top -n 1 -d 1 | grep %s";
    private final static FileManager fileManager = FileManager.getInstance();
    private final static Logger logger = Logger.getLogger(CPUTask.class);

    private String processName;
    private String sn;

    public void run() {
        String command = String.format(commandFormat, sn, processName);
        logger.debug("run command: " + command);

        String currentTime = System.currentTimeMillis() + "";
        String result = AppiumUtils.executeCmdWithResult(command);
        logger.debug("command result: " + command);

        if (CommonUtils.isEmpty(result)){
            logger.error("Execute command result is empty. We set the cpu usage is 0.");
        }else{
            String[] lines = result.split("\n");
            for(String line : lines){
                String[] procs = line.split("\\s+");
                String[] aaa = line.split("\t");
                //最后一个字段是进程名
                if(processName.equals(procs[procs.length -1])){
                    for(String s : procs){
                        if(s.contains("%")){
                            logger.debug("Usage is: " + Float.parseFloat(s.replace("%", "")));

                            CPUBean cpuBean = new CPUBean();
                            cpuBean.setTime(currentTime);
                            cpuBean.setProcessName(processName);
                            cpuBean.setUsagePercent(Float.parseFloat(s.replace("%", "")));

                            fileManager.writCpuBean(cpuBean);
                        }
                    }
                }
            }
        }
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
