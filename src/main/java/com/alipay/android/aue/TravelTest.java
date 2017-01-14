package com.alipay.android.aue;

import com.alipay.android.aue.bean.config.Appium;
import com.alipay.android.aue.interfaces.OnHookMethod;
import com.alipay.android.aue.task.CPUTask;
import com.alipay.android.aue.utils.AppiumUtils;
import com.alipay.android.aue.utils.DeviceInfoUtils;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;

/**
 * Created by bingo on 2016/10/10.
 */
public class TravelTest extends BaseTravelTest {

    private OnHookMethod setupCapabilitiesHook = new OnHookMethod() {
        @Override
        public void onCall(Object params) {
            try {
                HashMap<String, String> devices = (HashMap<String, String>) DeviceInfoUtils.getDivces();

                DesiredCapabilities capabilities = (DesiredCapabilities) params;
                capabilities.setCapability("platformName", configuration.getAppium().getPlatformName());
                capabilities.setCapability("app", configuration.getAppium().getAppPath());
                capabilities.setCapability("udid", configuration.getAppium().getSerialsNumber());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnHookMethod onActivityChanged = new OnHookMethod() {
        public void onCall(Object params) {
            HashMap<String, String> paramsAndResult = (HashMap<String, String>) params;
            String activity = paramsAndResult.get("activity");
            if (activity.equals("com.alipay.android.phone.voiceassistant.ui.VoiceAssistantActivity")){
                paramsAndResult.put("needTravel", "false");
            }

            //等待一秒，防止界面截图出现异常
            AppiumUtils.sleep(1 * 1000);
            captureScreen(activity);
        }
    };

    private OnHookMethod onElementClicked = new OnHookMethod() {
        @Override
        public void onCall(Object params) {
            HashMap<String, Object> paramsAndResult = (HashMap<String, Object>) params;
            WebElement element = (WebElement) paramsAndResult.get("element");
        }
    };

    @Override
    public void initRegister() {
        super.initRegister();

        register.register(Constant.HOOK_METHOD_SETUPCAPABILITIES, setupCapabilitiesHook);
        register.register(Constant.HOOK_METHOD_ONACTVITYCHANGE, onActivityChanged);
        register.register(Constant.HOOK_METHOD_ONCLICKELEMENT, onElementClicked);
    }

    @Override
    public void customInit() {
        super.customInit();

        fileManager.setCpuLogFilePath("/Users/bingo/Desktop/cpuLog.log");
        CPUTask cpuTask = new CPUTask();
        cpuTask.setSn(configuration.getAppium().getSerialsNumber());
        cpuTask.setProcessName(configuration.getAppium().getProcessName());

        taskManager.submitTask("CPUTask", cpuTask, 1000, true);
    }

    @Test
    public void 深度优先遍历_test(){
        AppiumUtils.sleep(5 * 1000);

        travel();
    }
}
