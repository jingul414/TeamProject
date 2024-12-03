package com.donghaeng.withme.screen;

import android.content.Context;
import android.content.SharedPreferences;

public class AppLaunchChecker {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_FIRST_RUN = "isFirstRun";

    public static boolean isFirstRun(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);

        // 최초 실행인 경우 플래그를 false로 변경
        if (isFirstRun) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_RUN, false);
            editor.apply();
        }

        return isFirstRun;
    }
}