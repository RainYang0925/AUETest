package com.alipay.android.aue.manager;

import com.alipay.android.aue.bean.CPUBean;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by bingo on 2016/10/8.
 */
public class FileManager {
    private Logger logger = Logger.getLogger(FileManager.class);

    private static FileManager fileManager;

    private PrintStream cpuUsagePrinter;
    private PrintStream memoryUsagePrinter;

    private FileManager() {
    }

    public static FileManager getInstance(){
        synchronized (FileManager.class) {
            if (fileManager == null) {
                fileManager = new FileManager();
            }

            return fileManager;
        }
    }

    public void setCpuLogFilePath(String filePath){
        try {
            cpuUsagePrinter = new PrintStream(new FileOutputStream(filePath));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            logger.error(e.toString());
        }catch (IOException ioe){
            ioe.printStackTrace();
            logger.error(ioe.toString());
        }
    }

    public void setMemoryLogFilePath(String filePath){
        try {
            memoryUsagePrinter = new PrintStream(new FileOutputStream(filePath));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            logger.error(e.toString());
        }catch (IOException ioe){
            ioe.printStackTrace();
            logger.error(ioe.toString());
        }
    }

    public void writCpuBean(CPUBean cpuBean){
        synchronized (cpuUsagePrinter) {
            cpuUsagePrinter.append(cpuBean.toString() + "\n");
        }
    }

    public void writeMemoryBean(){

    }
}
