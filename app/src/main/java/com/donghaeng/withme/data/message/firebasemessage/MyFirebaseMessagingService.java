package com.donghaeng.withme.data.message.firebasemessage;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.service.AlarmService;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.SettingsJobIntentService;
import com.donghaeng.withme.service.VolumeControlService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private int streamType;
    private final FireStoreManager fireStoreManager = FireStoreManager.getInstance();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            Log.e("FCM Data", "Data: " + remoteMessage.getData());
            // JobIntentService로 작업 위임
            Intent intent = new Intent();
            intent.putExtra("commandType", remoteMessage.getData().get("commandType"));
            intent.putExtra("commandValue", remoteMessage.getData().get("commandValue"));
            SettingsJobIntentService.enqueueWork(this, intent);
        }
    }

    private void handleCustomData(Map<String, String> data) {
        String commandType = data.get("commandType");
        String commandValue = data.get("commandValue");

        Log.e("FCM Data", "commandType: " + commandType);
        Log.e("FCM Data", "commandValue: " + commandValue);

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
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                Intent serviceIntent = new Intent(this, VolumeControlService.class);
                // streamType 값에 따라 적절한 streamType 설정
                switch (streamType) {
                    case AudioManager.STREAM_NOTIFICATION:
                        serviceIntent.putExtra("streamType", AudioManager.STREAM_NOTIFICATION);
                        break;
                    case AudioManager.STREAM_MUSIC:
                        serviceIntent.putExtra("streamType", AudioManager.STREAM_MUSIC);
                        break;
                    case AudioManager.STREAM_RING:
                        serviceIntent.putExtra("streamType", AudioManager.STREAM_RING);
                        break;
                }
                serviceIntent.putExtra("volume", volumePercent);
                serviceIntent.putExtra("delay", 0);

                startForegroundServiceCompat(serviceIntent);
            }
        } catch (NumberFormatException e) {
            Log.e("FCM Data", "Invalid volume value", e);
        }
    }


    private void handleSoundModeCommand(String value) {
        switch (value) {
            case "CALL":
                streamType = AudioManager.STREAM_RING;
                break;
            case "NOTIFICATION":
                streamType = AudioManager.STREAM_NOTIFICATION;
                break;
            case "MEDIA":
                streamType = AudioManager.STREAM_MUSIC;
                break;
            default:
                return;
        }
    }

    private void handleAlarmCommand(String value) {
        try {
            String[] timeParts = value.split(":");
            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                Intent serviceIntent = new Intent(this, AlarmService.class);
                serviceIntent.putExtra("hour", hour);
                serviceIntent.putExtra("minute", minute);

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

    @Override
    public void onNewToken(@NonNull String token) {
        Log.e("FCM NewToken", "Refreshed token: " + token);

        //토큰 최초 발행시(=앱 최초 실행시)에는 firestoreManager에 정보가 없으므로 올릴 수 없고 올릴 필요도 없음
        try {
            if (fireStoreManager.getPhone() != null) {
                fireStoreManager.changeInformation(fireStoreManager.getPhone(), "token", token, new FireStoreManager.firestoreCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Log.e("FCM NewToken", "New token saved successfully");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FCM NewToken", "New token save failed : " + e.getMessage());
                    }
                });
            }else{
                Log.e("FCM NewToken", "No phoneNumber");
            }
        } catch (Exception e) {
            Log.e("FCM NewToken", "Exception : " + e.getMessage());
        }
    }
}