package com.donghaeng.withme.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.donghaeng.withme.R;

public class BrightnessControlService extends Service {

    private static final String CHANNEL_ID = "brightness_control_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification("Brightness Control Active"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean autoLight = intent.getBooleanExtra("autoLight", false); // 자동 밝기 설정 여부
        int brightness = intent.getIntExtra("brightness", 128); // 기본 밝기 값 128
        int delay = intent.getIntExtra("delay", 10) * 1000; // 초 단위 딜레이, 기본값 10초

        // 10초 후 작업 실행
        new Handler().postDelayed(() -> {
            if (autoLight) {
                setAutoBrightness(true); // 자동 밝기 설정
            } else {
                setAutoBrightness(false); // 수동 밝기 설정
                if (brightness != -1) {
                    adjustBrightness(brightness); // 수동 밝기 값 적용
                }
            }
        }, delay);

        return START_NOT_STICKY;
    }

    private void setAutoBrightness(boolean enable) {
        try {
            int mode = enable ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            Log.d("BrightnessService", "AutoBrightness set to " + enable);
        } catch (Exception e) {
            Log.e("BrightnessService", "Error setting auto brightness", e);
        }
    }

    private void adjustBrightness(int brightness) {
        try {
            // 밝기 값 제한 (0 ~ 255)
            int adjustedBrightness = Math.max(0, Math.min(brightness, 255));
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, adjustedBrightness);

            Log.d("BrightnessControlService", "Brightness set to: " + adjustedBrightness);
        } catch (Exception e) {
            Log.e("BrightnessControlService", "Failed to adjust brightness", e);
        }
    }

    private Notification createNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Brightness Control Service")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_light_mode) // 적절한 아이콘 추가
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Brightness Control Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // 바인딩 사용 안 함
    }
}
