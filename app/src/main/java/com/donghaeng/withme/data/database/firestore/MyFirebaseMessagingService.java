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
        // 데이터 메시지 내용을 바탕으로 앱 내부 작업 처리
        String commandType = data.get("commandType");
        String commandValue = data.get("commandValue");

        Log.e("FCM Data", "commandType :" + commandType);
        Log.e("FCM Data", "commandValue :" + commandValue);

        // 필요한 로직 추가 (예: 데이터 처리, UI 업데이트 등)

        //startBrightnessControlService(false, Integer.parseInt(Objects.requireNonNull(commandValue)), 10);

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
