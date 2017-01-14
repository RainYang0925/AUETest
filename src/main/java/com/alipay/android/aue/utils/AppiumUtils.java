package com.alipay.android.aue.utils;


import org.dom4j.Element;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.Log4JLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by bingo on 2016/9/30.
 */
public class AppiumUtils {

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // ExceptionUtils.getErrorInfo(e);
        }
    }

    public final static InternalLogger logger = Log4JLoggerFactory
            .getDefaultFactory()
            .newInstance(AppiumUtils.class.toString());

    /**
     * Execute a command
     * @param command command string
     * @return process
     */
    public static Process executeCommand(String command){
        Process process = null;
        try {
            if (isWindow()){
                process = Runtime.getRuntime().exec("cmd.exe /C " + command);
            }else{
                process = Runtime.getRuntime().exec(
                  new String[]{
                          "/bin/bash", "-cl", command
                  });
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }

        return process;
    }

    /**
     * Execute a command and get result
     * @param command
     * @return
     */
    public static String executeCmdWithResult(String command){
        Process process = executeCommand(command);
        String result = read(process.getInputStream());
        if (result == null){
            return "";
        }

        return result;
    }

    /**
     * Get result from dealing process
     * @param process
     * @return standard_str: standard result; err_str: error stream
     */
    public static HashMap<String, String> getProcessResultStr(Process process){
        HashMap<String, String> resultHash = new HashMap<String, String>();

        resultHash.put("standard_str", read(process.getInputStream()));
        resultHash.put("err_str", read(process.getErrorStream()));

        return resultHash;
    }

    /**
     * Get string content from inputStream
     * @param inputStream
     * @return
     */
    private static String read(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                result += (line + "\n");
            }

            logger.info("read from stream: " + result);
        } catch (IOException e) {
            e.printStackTrace();

            logger.error(e.toString());
        } finally {

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.toString());
            }
        }

        return result;
    }

    /**
     * Get os type
     * @return
     */
    public static boolean isWindow(){
        if (System.getProperty("os.name").toLowerCase().indexOf("window") != -1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Get Xpath from uiNode
     * @return
     */
    public static String getXPath(Element ele){
//        String xpath = "";
//        xpath += "//" + uiNode.getNode().attributeValue("class")
//                + "[@resource-id='" + uiNode.getNode().attributeValue("resource-id") + "'"
//                + " and @index=" + uiNode.getNode().attributeValue("index");
//        if (uiNode.getNode().attributeValue("text").length() != 0){
//            xpath += " and @text='" + uiNode.getNode().attributeValue("text") + "'";
//        }
//        xpath += "]";
//
//        return xpath;

        if(ele.isRootElement() == true){
            return "/";
        }

        if (ele.elements().size() == 0){
            return getXPath(ele.getParent()) + "/"
                    + ele.attributeValue("class") + "[@resource-id='" + ele.attributeValue("resource-id") + "']";
        }

        String xPath = "";
        xPath += ele.attributeValue("class");
        if (ele.attributeValue("index") != null && ele.attributeValue("index").length() > 0){
            int index = Integer.parseInt(ele.attributeValue("index"));
            xPath += "[@index='" + index + "']";
        }
        return getXPath(ele.getParent()) + "/"
                + xPath;
    }
}
