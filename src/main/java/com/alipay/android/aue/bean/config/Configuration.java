package com.alipay.android.aue.bean.config;

import java.util.List;

/**
 * Created by bingo on 2016/10/9.
 */
public class Configuration {
    private Ignore ignore;
    private List<Task> tasks;
    private Appium appium;
    private Record record;

    public Appium getAppium() {
        return appium;
    }

    public void setAppium(Appium appium) {
        this.appium = appium;
    }

    public Ignore getIgnore() {
        return ignore;
    }

    public void setIgnore(Ignore ignore) {
        this.ignore = ignore;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
