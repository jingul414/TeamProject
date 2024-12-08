package com.donghaeng.withme.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;

public class RejectionManager {
    private static final String PREF_NAME = "rejection_prefs";
    private static final String KEY_END_TIME_PREFIX = "end_time_token_";
    private static RejectionManager instance;
    private final SharedPreferences prefs;
    private final Context context;

    private RejectionManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        cleanExpiredRejections();
    }

    public static synchronized RejectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new RejectionManager(context);
        }
        return instance;
    }

    // 거절 시간 설정 (분 단위)
    public void setRejection(String targetToken, int minutes) {
        long endTime = System.currentTimeMillis() + (minutes * 60 * 1000);
        prefs.edit().putLong(KEY_END_TIME_PREFIX + targetToken, endTime).apply();
    }

    // 거절 상태 확인
    public boolean isRejected(String targetToken) {
        long endTime = prefs.getLong(KEY_END_TIME_PREFIX + targetToken, 0);
        if (endTime == 0) return false;

        if (System.currentTimeMillis() > endTime) {
            removeRejection(targetToken);
            return false;
        }
        return true;
    }

    // 남은 시간 확인 (분 단위)
    public int getRemainingMinutes(String targetToken) {
        long endTime = prefs.getLong(KEY_END_TIME_PREFIX + targetToken, 0);
        if (endTime == 0) return 0;

        long remainingMillis = endTime - System.currentTimeMillis();
        return (int) Math.max(0, remainingMillis / (60 * 1000));
    }

    // 거절 상태 해제
    public void removeRejection(String targetToken) {
        prefs.edit().remove(KEY_END_TIME_PREFIX + targetToken).apply();
        sendStatusChangedBroadcast();
    }

    // 만료된 거절 상태 정리
    private void cleanExpiredRejections() {
        Map<String, ?> all = prefs.getAll();
        long now = System.currentTimeMillis();

        for (String key : all.keySet()) {
            if (key.startsWith(KEY_END_TIME_PREFIX)) {
                long endTime = prefs.getLong(key, 0);
                if (now > endTime) {
                    prefs.edit().remove(key).apply();
                    sendStatusChangedBroadcast();
                }
            }
        }
    }

    private void sendStatusChangedBroadcast() {
        Intent intent = new Intent("REJECTION_STATUS_CHANGED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}