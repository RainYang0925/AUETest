package com.alipay.android.aue.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bingo on 2016/10/4.
 */
public class DeviceInfoUtils {

    /**
     * This method start adb server
     */
    public final static void startADB() throws Exception{
        String output = AppiumUtils.executeCmdWithResult("adb start-server");
        String[] lines = output.split("\n");
        if(lines.length==1)
            System.out.println("adb service already started");
        else if(lines[1].equalsIgnoreCase("* daemon started successfully *"))
            System.out.println("adb service started");
        else if(lines[0].contains("internal or external command")){
            System.out.println("adb path not set in system varibale");
            System.exit(0);
        }
    }

    /**
     * This method stop adb server
     */
    public final static void stopADB() throws Exception{
        AppiumUtils.executeCommand("adb kill-server");
    }

    /**
     * This method return connected devices
     * @return hashmap of connected devices information
     */
    public final static Map<String, String> getDivces() throws Exception	{
        Map<String, String> devices = new HashMap<String, String>();

        startADB(); // start adb service
        String output = AppiumUtils.executeCmdWithResult("adb devices");
        String[] lines = output.split("\n");

        if(lines.length<=1){
            System.out.println("No Device Connected");
            stopADB();
            System.exit(0);	// exit if no connected devices found
        }

        for(int i=1;i<lines.length;i++){
            lines[i]=lines[i].replaceAll("\\s+", "");

            if(lines[i].contains("device")){
                lines[i]=lines[i].replaceAll("device", "");
                String deviceID = lines[i];
                String model = AppiumUtils.executeCmdWithResult("adb -s "+deviceID+" shell getprop ro.product.model").replaceAll("\\s+", "");
                String brand = AppiumUtils.executeCmdWithResult("adb -s "+deviceID+" shell getprop ro.product.brand").replaceAll("\\s+", "");
                String osVersion = AppiumUtils.executeCmdWithResult("adb -s "+deviceID+" shell getprop ro.build.version.release").replaceAll("\\s+", "");
                String deviceName = brand+" "+model;

                devices.put("deviceID"+i, deviceID);
                devices.put("deviceName"+i, deviceName);
                devices.put("osVersion"+i, osVersion);

                System.out.println("Following device is connected");
                System.out.println(deviceID+" "+deviceName+" "+osVersion+"\n");
            }else if(lines[i].contains("unauthorized")){
                lines[i]=lines[i].replaceAll("unauthorized", "");
                String deviceID = lines[i];

                System.out.println("Following device is unauthorized");
                System.out.println(deviceID+"\n");
            }else if(lines[i].contains("offline")){
                lines[i]=lines[i].replaceAll("offline", "");
                String deviceID = lines[i];

                System.out.println("Following device is offline");
                System.out.println(deviceID+"\n");
            }
        }
        return devices;
    }
}
