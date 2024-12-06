package com.donghaeng.withme.data.worker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.donghaeng.withme.service.AlarmService;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.VolumeControlService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackgroundSettingsWorker extends Worker {
    private static final String PREF_NAME = "SettingsPref";
    private static final String KEY_PENDING_SETTINGS = "pendingSettings";

    public BackgroundSettingsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("BackgroundWorker", "doWork started");
        try {
            if (isAppInForeground()) {
                Log.d("BackgroundWorker", "App is in foreground, skipping work");
                return Result.success();
            }
            Log.d("BackgroundWorker", "App is in background, proceeding with work");

            // 저장된 모든 설정값 가져오기
            Map<String, String> pendingSettings = getPendingSettings();
            Log.d("BackgroundWorker", "Pending settings: " + pendingSettings.toString());

            if (!pendingSettings.isEmpty()) {
                // 모든 설정 한번에 적용
                for (Map.Entry<String, String> entry : pendingSettings.entrySet()) {
                    String commandStr = entry.getKey();
                    Log.d("BackgroundWorker", "Processing command: " + commandStr);

                    String[] parts = commandStr.split(":");
                    if (parts.length >= 2) {
                        String commandType = parts[0];
                        String commandValue = parts[1];
                        if (parts.length == 3) {  // Alarm의 경우 시:분 형식
                            commandValue = parts[1] + ":" + parts[2];
                        }
                        Log.d("BackgroundWorker", "Applying setting - Type: " + commandType + ", Value: " + commandValue);
                        applySetting(commandType, commandValue);
                    }
                }
                Log.d("BackgroundWorker", "Applied batch settings: " + pendingSettings.toString());
            } else {
                Log.d("BackgroundWorker", "No pending settings to apply");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("BackgroundWorker", "Error executing work", e);
            e.printStackTrace();
            return Result.failure();
        }
    }
    private void clearNonAlarmSettings(Map<String, String> settings) {
        Map<String, String> newSettings = new HashMap<>();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            if (entry.getKey().startsWith("Alarm:")) {
                newSettings.put(entry.getKey(), entry.getValue());
            }
        }

        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PENDING_SETTINGS, new Gson().toJson(newSettings)).apply();
    }

    private void applySetting(String commandType, String commandValue) {
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
    }

    private void handleBrightnessCommand(String value) {
        Intent serviceIntent = new Intent(getApplicationContext(), BrightnessControlService.class);
        serviceIntent.putExtra("autoLight", false);
        serviceIntent.putExtra("brightness", Integer.parseInt(value));
        serviceIntent.putExtra("delay", 0);
        startForegroundService(serviceIntent);
    }

    private void handleAutoBrightnessCommand(String value) {
        Intent serviceIntent = new Intent(getApplicationContext(), BrightnessControlService.class);
        serviceIntent.putExtra("autoLight", Boolean.parseBoolean(value));
        serviceIntent.putExtra("brightness", -1);
        serviceIntent.putExtra("delay", 0);
        startForegroundService(serviceIntent);
    }

    private void handleVolumeCommand(String value) {
        Intent serviceIntent = new Intent(getApplicationContext(), VolumeControlService.class);
        serviceIntent.putExtra("volume", Integer.parseInt(value));
        serviceIntent.putExtra("streamType", AudioManager.STREAM_RING);
        serviceIntent.putExtra("delay", 0);
        startForegroundService(serviceIntent);
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
                Intent serviceIntent = new Intent(getApplicationContext(), AlarmService.class);
                serviceIntent.putExtra("hour", Integer.parseInt(timeParts[0]));
                serviceIntent.putExtra("minute", Integer.parseInt(timeParts[1]));
                startForegroundService(serviceIntent);
            }
        } catch (Exception e) {
            Log.e("BackgroundWorker", "Error handling alarm command", e);
        }
    }

    private boolean isAppInForeground() {
        try {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                Log.d("BackgroundWorker", "ActivityManager is null");
                return false;
            }

            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                Log.d("BackgroundWorker", "No running processes");
                return false;
            }

            String packageName = getApplicationContext().getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName)) {
                    Log.d("BackgroundWorker", "Found our process: importance = " + appProcess.importance);
                    // ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND = 100
                    // ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE = 200
                    return appProcess.importance <= 100;
                }
            }
            Log.d("BackgroundWorker", "Our process not found in running processes");
            return false;
        } catch (Exception e) {
            Log.e("BackgroundWorker", "Error checking foreground state", e);
            return false;
        }
    }

    private Map<String, String> getPendingSettings() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String settingsJson = prefs.getString(KEY_PENDING_SETTINGS, "{}");
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        return new Gson().fromJson(settingsJson, type);
    }

    private void clearPendingSettings() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PENDING_SETTINGS, "{}").apply();
    }

    private void scheduleNextWorkIfNeeded() {
        boolean scheduleNext = getInputData().getBoolean("schedule_next", false);
        if (scheduleNext) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(true)
                    .build();

            WorkManagerInitializer.startTestPeriodicWork(getApplicationContext());
        }
    }

    private void startForegroundService(Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("BackgroundWorker", "Starting foreground service: " + intent.getComponent());
                getApplicationContext().startForegroundService(intent);
            } else {
                Log.d("BackgroundWorker", "Starting service: " + intent.getComponent());
                getApplicationContext().startService(intent);
            }
        } catch (Exception e) {
            Log.e("BackgroundWorker", "Failed to start service", e);
            e.printStackTrace();
        }
    }
}
