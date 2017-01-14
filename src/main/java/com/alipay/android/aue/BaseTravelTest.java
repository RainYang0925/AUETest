package com.alipay.android.aue;

import com.alipay.android.aue.bean.UiNode;
import com.alipay.android.aue.bean.config.Configuration;
import com.alipay.android.aue.bean.config.Step;
import com.alipay.android.aue.manager.FileManager;
import com.alipay.android.aue.manager.RegisterCenter;
import com.alipay.android.aue.manager.TaskManager;
import com.alipay.android.aue.utils.AppiumUtils;
import com.alipay.android.aue.utils.CommonUtils;
import com.alipay.android.aue.utils.simlarity.SimHash;
import com.google.gson.JsonObject;
import io.appium.java_client.android.AndroidDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bingo on 2016/10/12.
 */
public class BaseTravelTest {
    public Logger logger = Logger.getLogger(BaseTravelTest.class);

    public DesiredCapabilities capabilities;
    public AndroidDriver androidDriver;

    public RegisterCenter register;
    //Element which will cause the change of activity
    public JsonObject activityBindPath;
    //effective element, include activityBindPath and interfaceBindEle;
    public JsonObject effectiveElement;

    public List<String> traveldActivity;

    public FileManager fileManager;
    public TaskManager taskManager;
    public Configuration configuration;

    //Travel迭代层数
    public Stack<String> activityStack;
    public Stack<String> iterationStack;

    /**
     * Load configuration for traveling
     */
    private final void loadAppiumTravelConfiguration() {
        // 初始化配置
        Yaml yaml = new Yaml();
        configuration = yaml.loadAs(CommonUtils.class.getResourceAsStream("/configuration.yml"), Configuration.class);
    }

    public void initRegister() {

    }

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configure(BaseTravelTest.class.getResourceAsStream("/log4j.properties"));
        //load configuration
        loadAppiumTravelConfiguration();

        logger.info("---------- setUp ------------");

        register = RegisterCenter.getInstance();
        //init register
        initRegister();

        capabilities = new DesiredCapabilities();
        //Insert the method to modify the capabilities
        register.invokeInstanceMethod(Constant.HOOK_METHOD_SETUPCAPABILITIES, capabilities);
        androidDriver = new AndroidDriver(
                new URL("http://" + configuration.getAppium().getHost()
                        + ":" + configuration.getAppium().getPort()
                        + "/wd/hub"), capabilities);
        androidDriver.manage().timeouts().implicitlyWait(configuration.getAppium().getInterval() , TimeUnit.SECONDS);

        activityBindPath = new JsonObject();
        effectiveElement = new JsonObject();
        traveldActivity = new ArrayList<String>();

        //File manager related
        fileManager = FileManager.getInstance();
        taskManager = TaskManager.getInstance();

        activityStack = new Stack<String>();

        customInit();
    }

    /**
     * Only for child to override
     */
    public void customInit() {

    }

    @After
    public void tearDown() {
        logger.info("---------- tearDown ------------");

        androidDriver.quit();
    }

    public boolean needIgnore(Element element) {
        String resourceId = element.attributeValue("resource-id");
        //没有resourceId的直接跳过，默认认为所有的可触发元素应该都是有resourceId的
        if (resourceId == null || resourceId.length() == 0) {
            return true;
        }
        //Not accessibility friendly
        String naf = element.attributeValue("NAF");
        if (naf != null && naf.equals("true")) {
            return true;
        }

        String ele_id = resourceId.split("id/")[1];
        String text = element.attributeValue("text");
        String cls = element.attributeValue("class");

        if (configuration.getIgnore().getClasses().contains(cls)) {
            return true;
        }

        if (configuration.getIgnore().getId().contains(ele_id)) {
            return true;
        }

        if (configuration.getIgnore().getText().contains(text)) {
            return true;
        }

        return false;
    }


    /**
     * MD5加密类
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    public static String toMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 这个是通过adb获取activity的stack的
     */
    public Stack<String> getActivityStack() {
        Stack<String> stack = new Stack<String>();
        Stack<String> checkStack = new Stack<String>();

        if (configuration.getAppium().getSerialsNumber() == null && configuration.getAppium().getProcessName() == null) {
            return stack;
        }
        String cmdFormat = "adb -s %s shell dumpsys activity activities | grep %s";
        String command = String.format(cmdFormat, configuration.getAppium().getSerialsNumber(), configuration.getAppium().getProcessName());

        while (stack.empty() == true || checkStack.empty() == true || compareStack(stack, checkStack) == false) {
            stack.clear();
            checkStack.clear();

            String result = AppiumUtils.executeCmdWithResult(command);
            String checkResult = AppiumUtils.executeCmdWithResult(command);
            getStackFromString(stack, result);
            getStackFromString(checkStack, checkResult);
        }

        return stack;
    }

    private void getStackFromString(Stack<String> stack, String result) {
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Activities=[ActivityRecord")) {
                Pattern pattern = Pattern.compile(configuration.getAppium().getProcessName() + "/([^ ]+)");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String activity = matcher.group();
                    activity = activity.substring(activity.indexOf("/") + 1);
                    stack.add(activity);
                }
            }
        }
    }

    public boolean compareStack(Stack<String> one, Stack<String> other) {
        //If the stacks are not the same length, then they won't be equal, easy first test case
        if (one.size() != other.size()) return false;

        for (int i = 0; i < one.size(); i++) {
            //Step through each item in both stacks, if any don't match return false
            if (!one.elementAt(i).equals(other.elementAt(i))) {
                return false;
            }
        }
        //Haven't returned yet, they must be equal
        return true;
    }

    /**
     * 截屏
     */
    public void captureScreen(String activity) {
        if (configuration.getRecord() != null && configuration.getRecord().getScreenCaptureDir() != null) {

            File captureDir = new File(configuration.getRecord().getScreenCaptureDir());
            if (captureDir.exists() == false) {
                captureDir.mkdirs();
            }

            String cmdFormat = "adb -s %s shell screencap -p | perl -pe 's/\\x0D\\x0A/\\x0A/g' > %s.png";
            String command = String.format(cmdFormat, configuration.getAppium().getSerialsNumber(), captureDir.getAbsolutePath() + File.separator + activity);

            AppiumUtils.executeCmdWithResult(command);
        }
    }

    /**
     * 记录当前系统中activity信息
     */
    public void loggerActivityStack() {
        logger.info("真实Activity Stack是: " + getActivityStack().toString() + "  记录的Activity栈是: " + activityStack.toString());
    }

    public void travel() {
        String currentActivity = androidDriver.currentActivity();

        if (traveldActivity.contains(currentActivity)) {
            androidDriver.pressKeyCode(4);
            logger.info(currentActivity + " has already entered, jump out.");
            return;
        } else {
            logger.info("Current activity is: " + currentActivity);
        }

        if (configuration.getIgnore().getLoadingActivity().contains(currentActivity)) {
            AppiumUtils.sleep(5 * 1000);//等待5秒
            logger.info("Wait loading, waiting activity is: " + androidDriver.currentActivity());
            currentActivity = androidDriver.currentActivity();
        }
        //到这个时候activity一般不会自动变化了，记录下来
        Stack<String> realStack = getActivityStack();
        realStack.pop();
        if (activityStack.contains(currentActivity)) {
            if (realStack.contains(currentActivity) == false) {
                logger.info("进入环了");
                return;
            }
        }
        activityStack.add(currentActivity);

        try {
            androidDriver.hideKeyboard();
        } catch (WebDriverException wde) {
            logger.error("Soft keyboard not present, cannot hide keyboard!!!");
        }

        final String currentPageSource = androidDriver.getPageSource();
        List<String> traveledElements = new ArrayList<String>();//已经便利过的元素，防止重新点击

        UiNode currentPageNode = null;

        try {
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(new ByteArrayInputStream(currentPageSource.getBytes("UTF-8")));
            currentPageNode = new UiNode(doc.getRootElement());
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            logger.error(uee.toString());
        } finally {
            if (currentPageNode == null) {
                //Error occurs.
                logger.error("Parsing node error. Current error is null.");
                activityStack.pop();
                return;
            }
        }

        //获取叶子节点
        List<UiNode> leafNodeList = CommonUtils.getLeafNode(currentPageNode);
        logger.info("Get leaf node, Number is: " + leafNodeList.size());
        //后续需要在这个地方对回去的leafNode进行一个排序

        //遍历叶子节点
        HashMap<String, Object> params = new HashMap<String, Object>();
        for (UiNode node : leafNodeList) {
            if (androidDriver.currentActivity().equals(currentActivity) == false) {
                if (getActivityStack().contains(currentActivity) == false) {
                    logger.error(activityStack.pop());
                    return;
                } else {
                    logger.error(androidDriver.currentActivity() + "不匹配CurrentActivity，按回退按钮");
                    androidDriver.pressKeyCode(4);
                }
            }

            //忽略范围判定
            if (needIgnore(node.getNode())) {
                continue;
            }

            final String xPath = AppiumUtils.getXPath(node.getNode());
            logger.info("当前元素的xPath是: " + xPath);

            if (traveledElements.contains(xPath)) {
                logger.info("已经访问过该元素.");
                continue;
            }

            try {
                WebElement ele = androidDriver.findElementByXPath(xPath);

                params.clear();
                params.put("element", ele);
                register.invokeInstanceMethod(Constant.HOOK_METHOD_ONCLICKELEMENT, params);
                if (params.keySet().contains("needClick") == true) {
                    boolean needClick = Boolean.parseBoolean((String) params.get("needClick"));
                    if (needClick == false) {
                        continue;
                    }
                }

                if (ele.isDisplayed() == false) {
                    logger.info("元素不可见，跳过。");
                    continue;
                }

                ele.click();
                AppiumUtils.sleep(1 * 1000);//等待一秒，等activity变化
                traveledElements.add(xPath);
                //每次点击完成之后都应该对页面进行检查，页面相似度
                if (currentActivity.equals(androidDriver.currentActivity())) {
                    pageSimilarity(currentPageSource);
                } else {
                    String newActivity = androidDriver.currentActivity();
                    logger.info("进入新的Activity: " + newActivity);

                    params.clear();
                    params.put("activity", newActivity);
                    register.invokeInstanceMethod(Constant.HOOK_METHOD_ONACTVITYCHANGE, params);

                    if (params.keySet().contains("needTravel") == false) {
                        //用户没有做处理，默认进入这个activity进行遍历
                        travel();
                    } else {
                        boolean needTravel = Boolean.parseBoolean((String) params.get("needTravel"));
                        if (needTravel) {
                            travel();
                        } else {
                            //不需要遍历就返回
                            androidDriver.pressKeyCode(4);
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                loggerActivityStack();
                logger.error(e.getMessage().toString());

                logger.info("当前的activity是: " + currentActivity);
                if (currentActivity.equals(androidDriver.currentActivity())) {
                    pageSimilarity(currentPageSource);
                }
            }
        }

        while (androidDriver.currentActivity().equals(currentActivity) == true) {
            androidDriver.pressKeyCode(4);
        }
        activityStack.pop();
        traveldActivity.add(currentActivity);
    }

    private void pageSimilarity(String currentPageSource) {
        //Activity没有发生变化，检查页面相似度，当前采用"汉明距离"计算
        long simhashPageSource = SimHash.computeOptimizedSimHashForString(currentPageSource);
        long simhashPageSourceCurrent = SimHash.computeOptimizedSimHashForString(androidDriver.getPageSource());

        int hanmingDistance = SimHash.hammingDistance(simhashPageSource, simhashPageSourceCurrent);

        if (hanmingDistance == 0) {
            //页面完全相似
            logger.info("页面没有发生变化，汉明距离为0。");
        } else if (hanmingDistance > 0 && hanmingDistance < 15) {
            //页面改变，但是变化不大
            logger.info("页面发生较小变化，汉明距离为: " + hanmingDistance);
        } else if (hanmingDistance >= 15) {
            //页面变化较大，此时应该重新遍历新的页面
            logger.info("页面发生变化明显，汉明距离为: " + hanmingDistance);
            //目前直接点击返回按钮
            androidDriver.pressKeyCode(4);
            logger.info("回退啦，然后继续下一个元素。");
        }
    }

    private void runConfiguratedTask(List<Step> steps) {
        for (Step step : steps) {
            if (step.getAction().equals(Constant.ACTION_CLICK)) {
                androidDriver.findElementByXPath(step.getParams()).click();
            } else if (step.getAction().equals(Constant.ACTION_INPUT)) {
                String[] params = step.getParams().split(">>");
                WebElement element = androidDriver.findElementByXPath(params[0]);
                element.sendKeys(params[1]);
            } else if (step.getAction().equals(Constant.ACTION_WAIT)) {
                AppiumUtils.sleep(Integer.parseInt(step.getParams()) * 1000);
            } else if (step.getAction().equals(Constant.ACTION_SWIPE_TOP)) {

            }
        }
    }
}
