package com.donghaeng.withme.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SettingsJobIntentService extends JobIntentService {
    private static final int JOB_ID = 1000;
    private static final int NOTIFICATION_ID = 100;
    private static final int BRIGHTNESS_NOTIFICATION_ID = 101;
    private static final int VOLUME_NOTIFICATION_ID = 102;
    private static final int ALARM_NOTIFICATION_ID = 103;
    private static final String CHANNEL_ID = "settings_control_channel";

    private static final String PREF_NAME = "VolumeSettings";
    private static final String PREF_SOUND_MODE = "sound_mode";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SettingsJobIntentService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // 서비스 시작 시 일반 알림
        showNotification();

        String commandType = intent.getStringExtra("commandType");
        String commandValue = intent.getStringExtra("commandValue");

        if (commandType == null || commandValue == null) return;

        try {
            switch (commandType) {
                case "Brightness":
                case "AutoBrightness":
                    showServiceSpecificNotification(
                            BRIGHTNESS_NOTIFICATION_ID,
                            "밝기 설정",
                            "화면 밝기를 변경하고 있습니다...");
                    if (commandType.equals("Brightness")) {
                        handleBrightnessCommand(commandValue);
                    } else {
                        handleAutoBrightnessCommand(commandValue);
                    }
                    writeLogData(commandType, commandValue);  // 로그 기록
                    break;
                case "Volume":
                case "SoundMode":
                    showServiceSpecificNotification(
                            VOLUME_NOTIFICATION_ID,
                            "볼륨 설정",
                            "소리 설정을 변경하고 있습니다...");
                    if (commandType.equals("Volume")) {
                        handleVolumeCommand(commandValue);
                    } else {
                        handleSoundModeCommand(commandValue);
                    }
                    writeLogData(commandType, commandValue);  // 로그 기록
                    break;
                case "Alarm":
                    showServiceSpecificNotification(
                            ALARM_NOTIFICATION_ID,
                            "알람 설정",
                            "알람을 설정하고 있습니다...");
                    handleAlarmCommand(commandValue);
                    writeLogData(commandType, commandValue);  // 로그 기록
                    break;
            }
        } catch (Exception e) {
            Log.e("JobIntentService", "Error handling command", e);
        }

    }

    private void showServiceSpecificNotification(int notificationId, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("설정 변경")
                .setContentText("설정을 변경하고 있습니다...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Settings Control Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }



    private void handleBrightnessCommand(String value) {
        try {
            int brightnessPercent = Integer.parseInt(value);
            Intent serviceIntent = new Intent(this, BrightnessControlService.class);
            serviceIntent.putExtra("autoLight", false);
            serviceIntent.putExtra("brightness", brightnessPercent);
            serviceIntent.putExtra("delay", 0);

            float percent = Math.round(((float) brightnessPercent / 255)*100);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showServiceSpecificNotification(
                        BRIGHTNESS_NOTIFICATION_ID,
                        "밝기 조절",
                        "밝기를 " + percent + "%로 설정합니다");
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showServiceSpecificNotification(
                    BRIGHTNESS_NOTIFICATION_ID,
                    "밝기 조절",
                    "자동 밝기를 " + (isAuto ? "활성화" : "비활성화") + " 합니다");
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void handleVolumeCommand(String value) {
        try {
            int volumePercent = Integer.parseInt(value);
            Intent serviceIntent = new Intent(this, VolumeControlService.class);

            // SharedPreferences에서 현재 설정된 mode 가져오기
            String mode = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .getString(PREF_SOUND_MODE, "MEDIA"); // 기본값은 MEDIA

            Log.d("JobIntentService", "Retrieved sound mode: " + mode);

            serviceIntent.putExtra("volume", volumePercent);
            serviceIntent.putExtra("mode", mode);
            serviceIntent.putExtra("delay", 0);
            String nofiMode;
            switch(mode){
                case "CALL":
                    nofiMode = "통화";
                    break;
                case "NOTIFICATION":
                    nofiMode = "알림";
                    break;
                case "MEDIA":
                    nofiMode = "미디어";
                    break;
                default:
                    nofiMode = "미디어";
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showServiceSpecificNotification(
                        VOLUME_NOTIFICATION_ID,
                        "볼륨 조절",
                        nofiMode + " 볼륨을 " + volumePercent + "%로 설정합니다");
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } catch (NumberFormatException e) {
            Log.e("JobIntentService", "Invalid volume value", e);
        }
    }

    private void handleSoundModeCommand(String value) {
        // SoundMode 설정을 SharedPreferences에 저장
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_SOUND_MODE, value)
                .apply();

        Log.d("JobIntentService", "Sound mode saved: " + value);
    }


    private void handleAlarmCommand(String value) {
        try {
            String[] timeParts = value.split(":");
            if (timeParts.length == 2) {
                Intent serviceIntent = new Intent(this, AlarmService.class);
                serviceIntent.putExtra("hour", Integer.parseInt(timeParts[0]));
                serviceIntent.putExtra("minute", Integer.parseInt(timeParts[1]));

                // Foreground Service 실행 전에 알림 표시
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
        } catch (Exception e) {
            Log.e("JobIntentService", "Invalid alarm format", e);
        }
    }


    private void writeLogData(String commandType, String commandValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String time = getCurrentTime();
        String logMessage = createLogMessage(commandType, commandValue);
        Log.d("SettingsJobIntentService", "로그 기록 시작");

        executorService.execute(() -> {
            try {
                CountDownLatch latch = new CountDownLatch(1);

                // UserRepository에서 Controller 정보 가져오기
                UserRepository repository = new UserRepository(this);
                repository.getAllUsers(users -> {
                    if (users != null && !users.isEmpty()) {
                        User controller = users.get(0);

                        // FireStoreManager를 통해 Controller의 전체 정보 가져오기
                        FireStoreManager fireStoreManager = FireStoreManager.getInstance();
                        fireStoreManager.getUserData(controller.getPhone());

                        // 데이터를 가져온 후에 Target 정보 확인 및 로그 기록
                        db.collection("user")
                                .document(EncrpytPhoneNumber.hashPhoneNumber(controller.getPhone()))
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        List<Map<String, Object>> targets = (List<Map<String, Object>>) documentSnapshot.get("targets");
                                        if (targets != null && !targets.isEmpty()) {
                                            Map<String, Object> target = targets.get(0);
                                            String targetId = (String) target.get("uid");

                                            // 로그 데이터 생성 및 저장
                                            Map<String, Object> logData = new HashMap<>();
                                            logData.put("control", logMessage);
                                            logData.put("name", controller.getName());
                                            logData.put("time", time);

                                            db.collection("log")
                                                    .document(targetId)
                                                    .collection(controller.getId())
                                                    .document(String.valueOf(System.currentTimeMillis()))
                                                    .set(logData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("SettingsJobIntentService", "로그 기록 성공!");
                                                        latch.countDown();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("SettingsJobIntentService", "로그 기록 실패", e);
                                                        latch.countDown();
                                                    });
                                        } else {
                                            Log.e("SettingsJobIntentService", "Target 정보가 없음");
                                            latch.countDown();
                                        }
                                    } else {
                                        Log.e("SettingsJobIntentService", "Controller 문서를 찾을 수 없음");
                                        latch.countDown();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("SettingsJobIntentService", "Controller 정보 가져오기 실패", e);
                                    latch.countDown();
                                });
                    } else {
                        Log.e("SettingsJobIntentService", "Controller 정보가 없음");
                        latch.countDown();
                    }
                });

                // 작업 완료 대기
                latch.await(10, TimeUnit.SECONDS);

            } catch (Exception e) {
                Log.e("SettingsJobIntentService", "로그 기록 실패", e);
            }
        });
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREAN);
        return outputFormat.format(currentDate);
    }

    private String createLogMessage(String commandType, String commandValue) {
        switch (commandType) {
            case "Brightness":
                float percent = Math.round(((float) Integer.parseInt(commandValue) / 255) * 100);
                return "밝기를 " + percent + "%로 설정";
            case "AutoBrightness":
                boolean isAuto = Boolean.parseBoolean(commandValue);
                return "자동 밝기를 " + (isAuto ? "활성화" : "비활성화");
            case "Volume":
                String mode = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        .getString(PREF_SOUND_MODE, "MEDIA");
                String volumeType;
                switch (mode) {
                    case "CALL":
                        volumeType = "통화";
                        break;
                    case "NOTIFICATION":
                        volumeType = "알림";
                        break;
                    case "MEDIA":
                        volumeType = "미디어";
                        break;
                    default:
                        volumeType = "미디어";
                        break;
                }
                return volumeType + " 볼륨을 " + commandValue + "%로 설정";
            case "SoundMode":
                String modeType;
                switch (commandValue) {
                    case "CALL":
                        modeType = "통화";
                        break;
                    case "NOTIFICATION":
                        modeType = "알림";
                        break;
                    case "MEDIA":
                        modeType = "미디어";
                        break;
                    default:
                        modeType = "미디어";
                        break;
                }
                return "소리 모드를 " + modeType + "로 변경";
            case "Alarm":
                return "알람을 " + commandValue + "로 설정";
            default:
                return "알 수 없는 설정 변경";
        }
    }
}
