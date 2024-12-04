package com.donghaeng.withme.data.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ControlAllowanceListChecker {
    private static final String PREFS_NAME = "ControlAllowanceListPrefs";
    /* KEYs */
    public static final String KEY_STORING_NOTICE = "storingNotice";
    public static final String KEY_VOLUME_MODE = "volumeMode";
    public static final String KEY_VOLUME_CONTROL = "volumeControl";
    public static final String KEY_BRIGHTNESS_CONTROL = "brightnessControl";
    public static final String KEY_SETTING_ALARM = "settingAlarm";

    private static SharedPreferences prefs;

    // 초기화
    private static void initPrefs(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setValue(Context context, String key, boolean value) {
        initPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply(); // 값 저장
        Log.d("ControlAllowanceListChecker", "Saved key: " + key + ", value: " + value);
    }

    public static boolean getValue(Context context, String key) {
        initPrefs(context);
        boolean value = prefs.getBoolean(key, true);
        Log.d("ControlAllowanceListChecker", "Loaded key: " + key + ", value: " + value);
        return value;
    }
}
