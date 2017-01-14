package com.alipay.android.aue.bean.config;

import java.util.List;

/**
 * Created by bingo on 2016/10/9.
 */
public class Task {
    private String name;
    private List<Step> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
