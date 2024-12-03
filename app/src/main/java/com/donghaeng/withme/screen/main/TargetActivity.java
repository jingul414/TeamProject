package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.ViewGroup;

import com.donghaeng.withme.R;

import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.screen.start.connect.TargetConnectFragment;
import com.donghaeng.withme.data.user.User;

public class TargetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_target);

        User user = getIntent().getParcelableExtra("user");

        // Fragment 초기화 로직을 분리
        if (savedInstanceState == null && getIntent().getStringExtra("fragmentName") != null) {
            if(getIntent().getStringExtra("fragmentName").equals("target_QR")){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, TargetConnectFragment.newInstance(user))
                        .commit();
            }
        }
        else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, TargetMainFragment.newInstance(user))
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top;
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 뒤로가기 애니메이션 설정
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ConnectInfoActivity에서 yes 버튼을 눌렀을 때 호출
    public void onConnectionComplete() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("fragmentName", "LoginFragment");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 이전 액티비티 종료
        startActivity(intent);
    }
}