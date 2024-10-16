package com.donghaeng.withme;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.donghaeng.withme.background.BackGroundWorkManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // control
        mContext = getApplicationContext();

        // WorkManager(BackGround)
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackGroundWorkManager.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);
    }
}