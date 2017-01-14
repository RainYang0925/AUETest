package com.alipay.android.aue.manager;

import com.alipay.android.aue.interfaces.OnHookMethod;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bingo on 2016/10/3.
 */
public class RegisterCenter {
    public Logger logger = Logger.getLogger(RegisterCenter.class);

    private static RegisterCenter registerCenter;
    private ClassLoader mainClassLoader = ClassLoader.getSystemClassLoader();
    private HashMap<String, List<OnHookMethod>> registerHash = new HashMap<String, List<OnHookMethod>>();

    private RegisterCenter(){}

    public static RegisterCenter getInstance(){
        synchronized (RegisterCenter.class) {
            if (registerCenter == null) {
                registerCenter = new RegisterCenter();
            }

            return registerCenter;
        }
    }


    public void register(String tag, OnHookMethod instance){
        if (registerHash.keySet().contains(tag)){
            // haven't check if the instance was already in the list.
            registerHash.get(tag).add(instance);
        }else{
            List<OnHookMethod> instanceList = new ArrayList<OnHookMethod>();
            instanceList.add(instance);

            registerHash.put(tag, instanceList);
        }
    }

    public void invokeInstanceMethod(String tag, Object params){
        if (registerHash.keySet().contains(tag)){
            for (OnHookMethod instance: registerHash.get(tag)) {
                instance.onCall(params);
            }
        }
    }
}
