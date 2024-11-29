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
        // 전달된 데이터를 가져오기
        int volume = intent.getIntExtra("volume", 50);
        int streamType = intent.getIntExtra("streamType", AudioManager.STREAM_RING);
        int delay = intent.getIntExtra("delay", 10) * 1000; // 초 단위 지연 시간

        // 딜레이 후 볼륨 조정 작업 수행
        new Handler().postDelayed(() -> adjustVolume(volume, streamType), delay);

        return START_NOT_STICKY; // 작업 완료 후 서비스가 자동 종료됨
    }

    private void adjustVolume(int volume, int streamType) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(streamType);
            int actualVolume = (volume * maxVolume) / 100;
            audioManager.setStreamVolume(streamType, actualVolume, 0);
            Log.d("VolumeControlService", "StreamType: " + streamType + "Volume set to: " + volume);
        }
        stopSelf(); // 작업 완료 후 서비스 종료
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
