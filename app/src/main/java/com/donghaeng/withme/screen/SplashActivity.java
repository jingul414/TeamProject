package com.donghaeng.withme.screen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.database.firestore.TokenManager;
import com.donghaeng.withme.data.app.ScreenChecker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        //firebase 토큰 가져오기, 확인
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    TokenManager.getInstance().setToken(token);
                    Log.e("FCM Token", "token: " + token);
                    // Firestore나 서버에 토큰 저장 로직 추가
                });

        ScreenChecker.getInitialScreen(this);
    }
}
