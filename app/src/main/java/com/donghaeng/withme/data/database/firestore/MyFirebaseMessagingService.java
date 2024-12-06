package com.donghaeng.withme.data.database.firestore;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.donghaeng.withme.data.command.SoundMode;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.service.AlarmService;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.VolumeControlService;
import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String PREF_NAME = "SettingsPref";
    private static final String KEY_PENDING_SETTINGS = "pendingSettings";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            String commandType = data.get("commandType");
            String commandValue = data.get("commandValue");

            // 새로운 설정값 저장
            addPendingSetting(commandType, commandValue);

            // 앱이 포그라운드 상태라면 즉시 적용
            if (isAppInForeground()) {
                handleCustomData(data);
            }
        }
    }

    private void handleCustomData(Map<String, String> data) {
        String commandType = data.get("commandType");
        String commandValue = data.get("commandValue");

        if (commandType == null || commandValue == null) return;

        try {
            switch (commandType) {
                case "Brightness":
                    handleBrightnessCommand(commandValue);
                    break;
                case "AutoBrightness":
                    handleAutoBrightnessCommand(commandValue);
                    break;
                case "Volume":
                    handleVolumeCommand(commandValue);
                    break;
                case "SoundMode":
                    handleSoundModeCommand(commandValue);
                    break;
                case "Alarm":
                    handleAlarmCommand(commandValue);
                    break;
            }
        } catch (Exception e) {
            Log.e("FCM Data", "Error handling command", e);
        }
    }

    private void handleBrightnessCommand(String value) {
        try {
            int brightnessPercent = Integer.parseInt(value);
            Intent serviceIntent = new Intent(this, BrightnessControlService.class);
            serviceIntent.putExtra("autoLight", false);
            serviceIntent.putExtra("brightness", brightnessPercent);
            serviceIntent.putExtra("delay", 0);

            startForegroundServiceCompat(serviceIntent);
        } catch (NumberFormatException e) {
            Log.e("FCM Data", "Invalid brightness value", e);
        }
    }

    private void handleAutoBrightnessCommand(String value) {
        boolean isAuto = Boolean.parseBoolean(value);
        Intent serviceIntent = new Intent(this, BrightnessControlService.class);
        serviceIntent.putExtra("autoLight", isAuto);
        serviceIntent.putExtra("brightness", -1);
        serviceIntent.putExtra("delay", 0);

        startForegroundServiceCompat(serviceIntent);
    }

    private void handleVolumeCommand(String value) {
        try {
            int volumePercent = Integer.parseInt(value);
            Intent serviceIntent = new Intent(this, VolumeControlService.class);
            serviceIntent.putExtra("volume", volumePercent);
            serviceIntent.putExtra("streamType", AudioManager.STREAM_RING);
            serviceIntent.putExtra("delay", 0);

            startForegroundServiceCompat(serviceIntent);
        } catch (NumberFormatException e) {
            Log.e("FCM Data", "Invalid volume value", e);
        }
    }

    private void handleSoundModeCommand(String value) {
        // SoundMode 처리
        switch (value) {
            case "CALL":
                handleVolumeCommand(String.valueOf(AudioManager.STREAM_RING));
                break;
            case "NOTIFICATION":
                handleVolumeCommand(String.valueOf(AudioManager.STREAM_NOTIFICATION));
                break;
            case "MEDIA":
                handleVolumeCommand(String.valueOf(AudioManager.STREAM_MUSIC));
                break;
        }
    }

    private void handleAlarmCommand(String value) {
        try {
            String[] timeParts = value.split(":");
            if (timeParts.length == 2) {
                Intent serviceIntent = new Intent(this, AlarmService.class);
                serviceIntent.putExtra("hour", Integer.parseInt(timeParts[0]));
                serviceIntent.putExtra("minute", Integer.parseInt(timeParts[1]));

                startForegroundServiceCompat(serviceIntent);
            }
        } catch (Exception e) {
            Log.e("FCM Data", "Invalid alarm format", e);
        }
    }

    private void startForegroundServiceCompat(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void addPendingSetting(String commandType, String commandValue) {
        try {
            SharedPreferences prefs = getApplicationContext()
                    .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            Map<String, String> pendingSettings = getPendingSettings();
            String key = commandType + ":" + commandValue;
            pendingSettings.put(key, commandValue);  // timestamp 대신 값 자체를 저장

            String settingsJson = new Gson().toJson(pendingSettings);
            prefs.edit().putString(KEY_PENDING_SETTINGS, settingsJson).apply();

            Log.d("FCM", "Added pending setting: " + key);
        } catch (Exception e) {
            Log.e("FCM", "Error adding pending setting", e);
        }
    }


    private Map<String, String> getPendingSettings() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String settingsJson = prefs.getString(KEY_PENDING_SETTINGS, "{}");
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        return new Gson().fromJson(settingsJson, type);
    }

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.e("FB MSG", "Refreshed token: " + token);
    }
}