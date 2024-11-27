package com.donghaeng.withme.screen.guide;

public class GuideItem {
    private String type;
    private String value;

    public GuideItem(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // getters
    public String getType() { return type; }
    public String getValue() { return value; }
}