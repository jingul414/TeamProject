package com.donghaeng.withme.screen.guide;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.donghaeng.withme.R;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guide);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new GuideFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void changeFragment(Fragment f){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations( // 프래그먼트 전환 애니메이션 설정
                R.anim.slide_in_right,  // 새로운 프래그먼트 들어올 때
                R.anim.slide_out_left,  // 현재 프래그먼트 나갈 때
                R.anim.slide_in_left,   // 뒤로가기할 때 새 프래그먼트 들어올 때
                R.anim.slide_out_right  // 뒤로가기할 때 현재 프래그먼트 나갈 때
        );
        transaction.replace(R.id.fragment_container, f);
        transaction.addToBackStack(null); // 뒤로가기 지원
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 뒤로가기 애니메이션 설정
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}