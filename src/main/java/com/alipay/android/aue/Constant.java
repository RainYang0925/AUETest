package com.alipay.android.aue;

/**
 * Created by bingo on 2016/9/30.
 */
public class Constant {
    public final static String HOOK_METHOD_SETUPCAPABILITIES = "SetupCapabilities";
    public final static String HOOK_METHOD_ONACTVITYCHANGE = "OnActivityChange";
    public final static String HOOK_METHOD_ONGETACTIVITYSTACK = "GetActivityStack";
    public final static String HOOK_METHOD_ONCLICKELEMENT = "OnClickElement";

    public final static String ACTION_CLICK = "Click";
    public final static String ACTION_INPUT = "Input";
    public final static String ACTION_WAIT = "Wait";

    public final static String ACTION_SWIPE_LEFT = "SwipeLeft";
    public final static String ACTION_SWIPE_RIGHT = "SwipeRight";
    public final static String ACTION_SWIPE_TOP = "SwipeTop";
    public final static String ACTION_SWIPE_BOTTOM = "SwipeBottom";

    //获取某台机器上某个程序的logcat
    public final static String LOGCAT_CMD_FORMAT = "adb -s %s logcat |grep `adb shell ps |grep %s |head -1 | cut -c10-15`";
}
