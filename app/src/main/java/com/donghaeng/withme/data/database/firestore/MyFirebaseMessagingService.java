package com.donghaeng.withme.data.database.firestore;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.service.BrightnessControlService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 데이터 메시지인지 확인
        if (!remoteMessage.getData().isEmpty()) {
            // 데이터 메시지 처리 로직
            Log.e("FCM Data", "Data: " + remoteMessage.getData());

            // 예: 앱 내부 로직 호출
            handleCustomData(remoteMessage.getData());
        }
    }

    private void handleCustomData(Map<String, String> data) {
        String commandType = data.get("commandType");
        String commandValue = data.get("commandValue");

        Log.e("FCM Data", "commandType :" + commandType);
        Log.e("FCM Data", "commandValue :" + commandValue);

        // 밝기 조절 명령인 경우
        if ("Brightness".equals(commandType) && commandValue != null) {
            try {
                // 퍼센트 값(0-100)을 실제 밝기 값(0-255)으로 변환
                int brightnessPercent = Integer.parseInt(commandValue);
                //int actualBrightness = (brightnessPercent * 255) / 100;

                startBrightnessControlService(false, brightnessPercent, 0);
            } catch (NumberFormatException e) {
                Log.e("FCM Data", "Invalid brightness value", e);
            }
        }
    }

    private void startBrightnessControlService(boolean autoLight, int brightness, int delay) {
        Intent serviceIntent = new Intent(this, BrightnessControlService.class);
        serviceIntent.putExtra("autoLight", autoLight);
        serviceIntent.putExtra("brightness", brightness);
        serviceIntent.putExtra("delay", delay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        // 새로운 FCM 토큰이 생성될 때 호출되는 메소드
        Log.e("FB MSG", "Refreshed token: " + token);
        // 새 토큰을 서버에 업데이트 (FireStore 등에 저장하는 로직 추가)

    }

//    private void startBrightnessControlService(boolean autoLight, int brightness, int delay) {
//        Intent serviceIntent = new Intent(context, BrightnessControlService.class);
//        serviceIntent.putExtra("autoLight", autoLight);
//        serviceIntent.putExtra("brightness", brightness);
//        serviceIntent.putExtra("delay", delay);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(serviceIntent);
//        } else {
//            context.startService(serviceIntent);
//        }
//    }
}
