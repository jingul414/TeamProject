package com.donghaeng.withme.data.message.firebasemessage;

public class SettingChangePayload {
    private final String key;
    private final boolean value;

    public SettingChangePayload(String key, boolean value) {
        this.key = key;
        this.value = value;
    }

    // Getter 메서드 추가
    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return value;
    }
}
