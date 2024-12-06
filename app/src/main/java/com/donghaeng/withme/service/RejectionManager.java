package com.donghaeng.withme.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class RejectionManager {
    private static final String PREF_NAME = "rejection_prefs";
    private static final String KEY_END_TIME_PREFIX = "end_time_token_";  // prefix 변경
    private static RejectionManager instance;
    private final SharedPreferences prefs;

    private RejectionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cleanExpiredRejections();
    }

    public static synchronized RejectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new RejectionManager(context);
        }
        return instance;
    }

    // targetToken으로 파라미터 이름 변경
    public void setRejection(String targetToken, int minutes) {
        long endTime = System.currentTimeMillis() + (minutes * 60 * 1000);
        prefs.edit().putLong(KEY_END_TIME_PREFIX + targetToken, endTime).apply();
    }

    // targetToken으로 파라미터 이름 변경
    public boolean isRejected(String targetToken) {
        long endTime = prefs.getLong(KEY_END_TIME_PREFIX + targetToken, 0);
        if (endTime == 0) return false;

        if (System.currentTimeMillis() > endTime) {
            removeRejection(targetToken);
            return false;
        }
        return true;
    }

    // targetToken으로 파라미터 이름 변경
    public int getRemainingMinutes(String targetToken) {
        long endTime = prefs.getLong(KEY_END_TIME_PREFIX + targetToken, 0);
        if (endTime == 0) return 0;

        long remainingMillis = endTime - System.currentTimeMillis();
        return (int) (remainingMillis / (60 * 1000));
    }

    // targetToken으로 파라미터 이름 변경
    private void removeRejection(String targetToken) {
        prefs.edit().remove(KEY_END_TIME_PREFIX + targetToken).apply();
    }

    private void cleanExpiredRejections() {
        Map<String, ?> all = prefs.getAll();
        long now = System.currentTimeMillis();

        for (String key : all.keySet()) {
            if (key.startsWith(KEY_END_TIME_PREFIX)) {
                long endTime = prefs.getLong(key, 0);
                if (now > endTime) {
                    prefs.edit().remove(key).apply();
                }
            }
        }
    }
}