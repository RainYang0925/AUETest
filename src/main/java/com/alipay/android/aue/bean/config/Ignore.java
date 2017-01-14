package com.alipay.android.aue.bean.config;

import java.util.List;

/**
 * Created by bingo on 2016/10/9.
 */
public class Ignore {
    private List<String> id;
    private List<String> classes;
    private List<String> text;
    private List<String> loadingActivity;

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<String> getLoadingActivity() {
        return loadingActivity;
    }

    public void setLoadingActivity(List<String> loadingActivity) {
        this.loadingActivity = loadingActivity;
    }
}
