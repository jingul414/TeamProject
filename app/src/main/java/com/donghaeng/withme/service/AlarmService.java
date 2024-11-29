package com.donghaeng.withme.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.donghaeng.withme.screen.main.ControllerActivity;

public class AlarmService extends Service {
    private static final String CHANNEL_ID = "AlarmServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int hour = intent.getIntExtra("hour", 0);
        int minute = intent.getIntExtra("minute", 0);

        // Foreground 서비스 시작을 위한 알림
        startForeground(NOTIFICATION_ID, createInitialNotification());

        // 5초 후에 알람 설정 알림 표시
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showAlarmNotification(hour, minute);
        }, 5000);

        return START_NOT_STICKY;
    }

    private Notification createInitialNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("알람 서비스 실행 중")
                .setContentText("알람 설정을 준비하고 있습니다...")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void showAlarmNotification(int hour, int minute) {
        Intent notificationIntent = new Intent(this, ControllerActivity.class)
                .putExtra("SET_ALARM", true)
                .putExtra("ALARM_HOUR", hour)
                .putExtra("ALARM_MINUTE", minute);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        @SuppressLint("DefaultLocale") Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("알람 설정")
                .setContentText(String.format("%02d:%02d 알람을 설정하려면 터치하세요", hour, minute))
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(2, notification); // 다른 ID 사용

        // 서비스 종료
        new Handler(Looper.getMainLooper()).postDelayed(this::stopSelf, 1000);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "알람 서비스",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("알람 설정을 위한 서비스 채널");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}