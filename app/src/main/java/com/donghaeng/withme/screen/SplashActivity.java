package com.donghaeng.withme.screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.guide.GuideBook;
import com.donghaeng.withme.data.database.room.guide.GuideBookDatabase;
import com.donghaeng.withme.data.database.room.guide.GuideBookRepository;
import com.donghaeng.withme.screen.start.StartActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
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
        checkFirstRun();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }

    private void checkFirstRun() {
        guideBookDatabase = GuideBookDatabase.getInstance(this);
        if (AppLaunchChecker.isFirstRun(this)) {
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
