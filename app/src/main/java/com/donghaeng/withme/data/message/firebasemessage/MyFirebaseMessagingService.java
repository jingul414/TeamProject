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