package com.donghaeng.withme.screen.main;

import com.donghaeng.withme.screen.guide.ListItem;

public class TargetListItem extends ListItem {
    private static final int TYPE_LOG = 4; // 제어 기록 타입

    private String control_log;
    private String name;
    private String time;


    public TargetListItem(String id, String control_log, String name, String time) {
        super(id, TYPE_LOG, control_log);
        this.control_log = control_log;
        this.name = name;
        this.time = time;
    }
    
    public String getControl_log() {
        return control_log;
    }
    public String getName() {
        return name;
    }
    public String getTime(){
        return time;
    }
}
