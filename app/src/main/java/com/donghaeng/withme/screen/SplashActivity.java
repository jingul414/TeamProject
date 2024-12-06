package com.donghaeng.withme.screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.database.firestore.TokenManager;
import com.donghaeng.withme.data.app.ScreenChecker;
import com.donghaeng.withme.data.worker.WorkManagerInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int WRITE_SETTINGS_REQUEST_CODE = 1000;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // worker 실행
        WorkManagerInitializer.startTestPeriodicWork(this);  // 테스트용 1분 주기


        // 권한 체크 시작
        checkPermissions();
    }

    private void checkPermissions() {
        // 시스템 설정 권한 체크
        if (!Settings.System.canWrite(this)) {
            requestWriteSettingsPermission();
        } else if (!checkNotificationPermission()) {
            // 시스템 설정 권한은 있지만 알림 권한이 없는 경우
            requestNotificationPermission();
        } else {
            // 모든 권한이 있는 경우
            initializeApp();
        }
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Android 13 미만에서는 true 반환
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE);
        }
    }

    private void requestWriteSettingsPermission() {
        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage("앱 화면 밝기 조절, 소리 조절, 알람 설정 등 앱의 기능들을 사용하기 위해 시스템 설정 권한이 필요합니다. 설정에서 권한을 허용해주시겠습니까?")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, WRITE_SETTINGS_REQUEST_CODE);
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    // 권한이 필수적이므로 앱 종료
                    Toast.makeText(this,
                            "권한 허용이 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setCancelable(false) // 뒤로 가기 버튼으로 다이얼로그를 닫을 수 없게 설정
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 허용됨
                initializeApp();
            } else {
                // 알림 권한 거부됨
                Toast.makeText(this,
                        "알림 권한이 필요합니다. 설정에서 권한을 허용해주세요.",
                        Toast.LENGTH_LONG).show();
                // 설정 화면으로 이동하는 옵션 제공
                showNotificationSettings();
            }
        }
    }

    private void showNotificationSettings() {
        new AlertDialog.Builder(this)
                .setTitle("알림 권한 필요")
                .setMessage("앱 사용을 위해 알림 권한이 필요합니다. 설정에서 권한을 허용해주시겠습니까?")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    finish(); // 앱 종료
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_SETTINGS_REQUEST_CODE) {
            if (Settings.System.canWrite(this)) {
                // 시스템 설정 권한 허용됨, 다음으로 알림 권한 체크
                if (!checkNotificationPermission()) {
                    requestNotificationPermission();
                } else {
                    initializeApp();
                }
            } else {
                // 시스템 설정 권한 거부됨
                finish();
            }
        }
    }

    private void initializeApp() {
        // Firebase 초기화 및 기타 설정
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed",
                                task.getException());
                        return;
                    }
                    String token = task.getResult();
                    TokenManager.getInstance().setToken(token);
                    Log.e("FCM Token", "token: " + token);

                    ScreenChecker.getInitialScreen(this);
                });
    }
}