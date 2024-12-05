package com.donghaeng.withme.screen.main;

import com.donghaeng.withme.screen.guide.ListItem;

public class ControlListItem extends ListItem {
    private static final int TYPE_CONTROL = 3; // 컨트롤 패널용 타입
    
    private String profileImage;
    private boolean alarmEnabled;
    private String alarmTime;
    
    public ControlListItem(String token, String title, String profileImage) {
        super(token, TYPE_HEADER, title);
        this.profileImage = profileImage;
        this.alarmTime = "AM 10:10"; // 기본값
        this.alarmEnabled = false;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public String getAlarmTime() {
        return alarmTime;
    }
    
    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }
    
    public boolean isAlarmEnabled() {
        return alarmEnabled;
    }
    
    public void setAlarmEnabled(boolean enabled) {
        this.alarmEnabled = enabled;
    }
}
