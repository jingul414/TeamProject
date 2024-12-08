package com.donghaeng.withme.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.donghaeng.withme.R;

public class VolumeControlService extends Service {

    private static final String CHANNEL_ID = "VolumeControlServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification("Volume Control Active"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int volume = intent.getIntExtra("volume", 50);
        String mode = intent.getStringExtra("mode"); // CALL, NOTIFICATION, MEDIA
        int delay = intent.getIntExtra("delay", 10) * 1000;

        // 로그 추가
        Log.d("VolumeControlService", "Received - Mode: " + mode + ", Volume: " + volume);

        if (mode == null) {
            Log.e("VolumeControlService", "Mode is null");
            stopSelf();
            return START_NOT_STICKY;
        }

        new Handler().postDelayed(() -> adjustVolumeByMode(volume, mode), delay);

        return START_NOT_STICKY;
    }

    private void adjustVolumeByMode(int volume, String mode) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            Log.d("VolumeControlService", "Adjusting volume - Mode: " + mode + ", Volume: " + volume);

            switch (mode) {
                case "CALL":
                    Log.d("VolumeControlService", "Setting CALL volumes");
                    adjustStreamVolume(audioManager, AudioManager.STREAM_RING, volume);
                    adjustStreamVolume(audioManager, AudioManager.STREAM_ALARM, volume);
                    break;
                case "NOTIFICATION":
                    Log.d("VolumeControlService", "Setting NOTIFICATION volumes");
                    adjustStreamVolume(audioManager, AudioManager.STREAM_NOTIFICATION, volume);
                    adjustStreamVolume(audioManager, AudioManager.STREAM_SYSTEM, volume);
                    break;
                case "MEDIA":
                    Log.d("VolumeControlService", "Setting MEDIA volume");
                    adjustStreamVolume(audioManager, AudioManager.STREAM_MUSIC, volume);
                    break;
                default:
                    Log.e("VolumeControlService", "Unknown mode: " + mode);
                    break;
            }
        } else {
            Log.e("VolumeControlService", "AudioManager is null");
        }
        stopSelf();
    }

    private void adjustStreamVolume(AudioManager audioManager, int streamType, int volume) {
        try {
            int maxVolume = audioManager.getStreamMaxVolume(streamType);
            int actualVolume = (volume * maxVolume) / 100;

            // 볼륨 범위 확인
            actualVolume = Math.max(0, Math.min(actualVolume, maxVolume));

            audioManager.setStreamVolume(streamType, actualVolume, 0);
            Log.d("VolumeControlService", "StreamType: " + streamType +
                    " MaxVolume: " + maxVolume +
                    " Requested: " + volume +
                    " Actual: " + actualVolume);
        } catch (Exception e) {
            Log.e("VolumeControlService", "Error adjusting volume for stream " + streamType, e);
        }
    }

    private Notification createNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Volume Control Service")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_volume) // 알림 아이콘
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Volume Control Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Bound Service가 아니라면 null 반환
    }
}
