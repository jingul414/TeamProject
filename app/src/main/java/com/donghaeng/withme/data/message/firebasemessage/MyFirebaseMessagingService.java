package com.donghaeng.withme.data.message.firebasemessage;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.donghaeng.withme.service.RejectionManager;

import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.service.AlarmService;
import com.donghaeng.withme.service.BrightnessControlService;
import com.donghaeng.withme.service.SettingsJobIntentService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    private final FireStoreManager fireStoreManager = FireStoreManager.getInstance();
    private UserRepository userRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        userRepository = new UserRepository(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().isEmpty()) return;

        Log.d(TAG, "Data: " + remoteMessage.getData());

        userRepository.getAllUsers(users -> {
            if (users != null && !users.isEmpty()) {
                User otherUser = users.get(0);

                // otherUser의 타입을 확인하여 메시지 처리
                if (otherUser.getUserType() == UserType.TARGET) {
                    // 내가 Controller이고 Target으로부터 메시지를 받은 경우
                    handleTargetMessage(remoteMessage.getData(), otherUser.getToken());
                } else if (otherUser.getUserType() == UserType.CONTROLLER) {
                    // 내가 Target이고 Controller로부터 메시지를 받은 경우
                    handleControllerMessage(remoteMessage.getData());
                }
            }
        });
    }

    private void handleTargetMessage(Map<String, String> data, String targetToken) {
        String commandType = data.get("commandType");
        if ("reject".equals(commandType)) {
            int rejectTime = Integer.parseInt(data.get("commandValue"));

            // SharedPreferences에 저장
            RejectionManager.getInstance(this).setRejection(targetToken, rejectTime);

            // UI 갱신을 위한 브로드캐스트 전송
            Intent intent = new Intent("REJECTION_STATUS_CHANGED");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            Log.d(TAG, String.format("제어 거절 설정: %d분", rejectTime));
        }
    }

    private void handleControllerMessage(Map<String, String> data) {
        // Controller로부터의 제어 명령을 SettingsJobIntentService로 전달
        Intent intent = new Intent();
        intent.putExtra("commandType", data.get("commandType"));
        intent.putExtra("commandValue", data.get("commandValue"));
        SettingsJobIntentService.enqueueWork(this, intent);
    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.e("FCM NewToken", "Refreshed token: " + token);

        //토큰 최초 발행시(=앱 최초 실행시)에는 firestoreManager에 정보가 없으므로 올릴 수 없고 올릴 필요도 없음
        try {
            if (fireStoreManager.getPhone() != null) {
                // 내 토큰 변경
                fireStoreManager.changeInformation(fireStoreManager.getPhone(), "token", token, new FireStoreManager.firestoreCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        Log.e("FCM NewToken", "New token saved successfully");
//                        UserRepository repository = new UserRepository(getApplicationContext());
//                        repository.getAllUsers((opponents)-> {
//                            for (User opponent : opponents) {
//                                fireStoreManager.changeOpponentToken(opponent, token);
//                            }
//                        });
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