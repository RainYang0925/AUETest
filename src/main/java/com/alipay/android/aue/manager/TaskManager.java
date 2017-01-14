package com.alipay.android.aue.manager;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created by bingo on 2016/9/30.
 */
public class TaskManager {
    private final static int MAX_THREAD_NUMBER = 10;

    public Logger logger = Logger.getLogger(TaskManager.class);

    private static TaskManager taskManager;
    private ScheduledExecutorService executorService;
    private HashMap<String, ScheduledFuture> futureHashMap;

    private TaskManager(){
        executorService = Executors.newScheduledThreadPool(MAX_THREAD_NUMBER);
        futureHashMap = new HashMap<String, ScheduledFuture>();
    }

    public static TaskManager getInstance(){
        synchronized (TaskManager.class) {
            if (taskManager == null) {
                taskManager = new TaskManager();
            }
            return taskManager;
        }
    }

    /**
     * Submit task
     * @param taskName
     * @param task
     * @param period period time during each running-time
     * @param forceStop if task already in task manager, force stop the old task or not ?
     */
    public void submitTask(String taskName, Runnable task, int period, boolean forceStop){
        ScheduledFuture future = executorService.scheduleAtFixedRate(task, 0, period, TimeUnit.MILLISECONDS);

        if (futureHashMap.keySet().contains(taskName)){
            //task already in task manager
            if (forceStop) {
                futureHashMap.get(taskName).cancel(true);
                //remove old task
                futureHashMap.remove(taskName);
                //add new task
                futureHashMap.put(taskName, future);
            }
        }else{
            futureHashMap.put(taskName, future);
        }
        logger.info("submit task: " + taskName + " into taskManager.");
    }

    /**
     * Stop task
     * @param taskName
     * @param forceStop if task already running, force stop the old task or not ?
     */
    public void stopTask(String taskName, boolean forceStop){
        if (!futureHashMap.keySet().contains(taskName)){
            logger.error("No task found which you want to stop: " + taskName);
            return;
        }else{
            futureHashMap.get(taskName).cancel(forceStop);
        }
    }
}
