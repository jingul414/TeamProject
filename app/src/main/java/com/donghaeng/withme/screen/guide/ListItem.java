package com.donghaeng.withme.screen.guide;

import java.util.ArrayList;
import java.util.List;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String title;
    private List<String> subItems;
    private boolean isExpanded;
    private String id;

    public ListItem(String id, int type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subItems = new ArrayList<>();
        this.isExpanded = false;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setSubItems(List<String> subItems) {
        this.subItems = subItems;
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}