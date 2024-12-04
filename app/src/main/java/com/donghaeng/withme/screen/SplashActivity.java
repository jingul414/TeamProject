package com.donghaeng.withme.screen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AppFirstLaunchChecker;
import com.donghaeng.withme.data.guide.GuideBook;
import com.donghaeng.withme.data.database.room.guide.GuideBookDatabase;
import com.donghaeng.withme.data.database.room.guide.GuideBookRepository;
import com.donghaeng.withme.screen.start.StartActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 1;
    private GuideBookDatabase guideBookDatabase;
    private GuideBookRepository guideBookRepository;
    private FirebaseAppCheck firebaseAppCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);
        firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        // 최초 실행 여부 확인
//        checkFirstRun(); 가이드 화면에서 불러오게 수정

        //firebase 토큰 가져오기, 확인
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.e("FCM Token", "token: " + token);
                    // Firestore나 서버에 토큰 저장 로직 추가
                });

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }

    private void checkFirstRun() {
        guideBookDatabase = GuideBookDatabase.getInstance(this);
        if (AppFirstLaunchChecker.isFirstRun(this)) {
            Toast.makeText(this, "앱 최초 실행", Toast.LENGTH_SHORT).show();
            // 필요한 초기 설정 작업 수행
            downloadGuideBooks();
        }
    }
    private void downloadGuideBooks() {
        FirebaseFirestore externalDatabase = FirebaseFirestore.getInstance();
        guideBookRepository = new GuideBookRepository(this);

        externalDatabase.collection("app_guide_book")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // 중복 검사 => 보류 : 모두 조회 = 속도 저하
//                            if(document.get("title", String.class).equals(guideBookRepository.getAppGuides().get(0).getTitle())) {}
                            GuideBook guideBook = document.toObject(GuideBook.class);
                            guideBookRepository.insert(guideBook);
                        }
                    }
                });
        externalDatabase.collection("smartphone_guide_book")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            GuideBook guideBook = document.toObject(GuideBook.class);
                            guideBookRepository.insert(guideBook);
                        }
                    }
                });
        externalDatabase.collection("controller_instruction")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            GuideBook guideBook = document.toObject(GuideBook.class);
                            guideBookRepository.insert(guideBook);
                        }
                    }
                });
    }
}
