![dog](http://blog.qiguosh.com/media/dog.png)

[AUETest Framework](https://github.com/CodingBingo/AUETest)
----
AUETest Framework is a framework which can work with [appium](http://appium.io/) to do test on real devices.

## Support Platform
* Android

I will do more develops on this project to support more platform.

## Features
1. Easy to do your own develop with this framework.
2. Written in Java and easy to do some personal configuration.
3. Stable Depth-first traversal algorithm.

## What I will do next?
* Record the activity structure and draw a mindmap.
* Record effective elements to make the test more quickly.
* Support ios
* .......

<!--more-->

## Quit Start
* Install Appium.
* Android environment.
* Clone this project to your local.

```
    git clone https://github.com/CodingBingo/AUETest.git
```
* Modify configuration.yml file according to the app you want to do tests

```
# Elements contains these attribute will be ignored
ignore:
  id:
    - title_bar_back_button
    - social_search_back_button
    - itemDevider
    - back_press
    - bt_back
    - h5_tv_nav_back
    - h5_lv_nav_back
    - close
    - sepelateLine
    - title_bar_left_line
    - title_bar_title

  classes:
    - android.widget.EditText

  loadingActivity:
    - com.alipay.mobile.nebulacore.ui.H5Activity

  text:
    - 返回
    - 拨打银行电话

# Record file location
record:
  videoRecordDir: Your location
  screenCaptureDir: Your location

# Appium configurations
appium:
  appPath: ~/Downloads/bingo.apk
  host: 127.0.0.1
  port: 4723
  # 等待延时
  interval: 3
  platformName: android
  serialsNumber: PBV5T16425011102
  processName: com.eg.android.AlipayGphone
```
* Write a test class and extend from BaseTravelTest

```
public class TravelTest extends BaseTravelTest {
    // Get a OnHookMethod object
    private OnHookMethod setupCapabilitiesHook = new OnHookMethod() {
        @Override
        public void onCall(Object params) {
            try {
                HashMap<String, String> devices = (HashMap<String, String>) DeviceInfoUtils.getDivces();

                DesiredCapabilities capabilities = (DesiredCapabilities) params;
                capabilities.setCapability("deviceName", devices.get("deviceName1"));
                capabilities.setCapability("platformName", "android");
                capabilities.setCapability(CapabilityType.VERSION, devices.get("osVersion1"));
                capabilities.setCapability("app", "/Users/bingo/Downloads/Alipay_993092805_Release_201609281548.apk");
                capabilities.setCapability("udid", devices.get("deviceID1"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnHookMethod onActivityChanged = new OnHookMethod() {
        public void onCall(Object params) {
            // Not only you can get params from the params-object,
            //but also you can deliver your result to the travel method.
            HashMap<String, String> paramsAndResult = (HashMap<String, String>) params;
            String activity = paramsAndResult.get("activity");
            if (activity.equals("com.alipay.android.phone.voiceassistant.ui.VoiceAssistantActivity")){
                paramsAndResult.put("needTravel", "false");
            }

            captureScreen(activity);
        }
    };

    @Override
    public void initRegister() {
        super.initRegister();
        //registe hookmethod object to manager to do some customize
        register.register(Constant.HOOK_METHOD_SETUPCAPABILITIES, setupCapabilitiesHook);
        register.register(Constant.HOOK_METHOD_ONACTVITYCHANGE, onActivityChanged);
    }

    @Override
    public void customInit() {
        super.customInit();
        //Set cpu log file path
        //Will move to configuration file
        fileManager.setCpuLogFilePath("/Users/bingo/Desktop/cpuLog.log");
        CPUTask cpuTask = new CPUTask();
        cpuTask.setSn(configuration.getAppium().getSerialsNumber());
        cpuTask.setProcessName(configuration.getAppium().getProcessName());

        taskManager.submitTask("CPUTask", cpuTask, 1000, true);
    }

    @Test
    public void dfs_test(){
        AppiumUtils.sleep(5 * 1000);
        //Call main travel method
        travel();
    }
}
```
## Star and fork are welcomed