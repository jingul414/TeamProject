package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.start.connect.ControllerConnectFragment;
import com.donghaeng.withme.user.Undefined;
import com.donghaeng.withme.user.User;

public class ControllerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller);
        handleIntent(getIntent());

        User user = getIntent().getParcelableExtra("user");

        // Fragment 초기화 로직을 분리
        if (savedInstanceState == null && getIntent().getStringExtra("fragmentName") != null) {
            if(getIntent().getStringExtra("fragmentName").equals("controller_QR")){
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, ControllerConnectFragment.newInstance(user))
                        .commit();
            }
        } else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new ControlFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top;
            return insets;
        });
    }

    // 알람 설정 관련 메소드 들
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("SET_ALARM", false)) {
            int hour = intent.getIntExtra("ALARM_HOUR", -1);
            int minute = intent.getIntExtra("ALARM_MINUTE", -1);

            if (hour != -1 && minute != -1) {
                setAlarm(hour, minute);
            }
        }
    }
    private void setAlarm(int hour, int minute) {
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "알람")
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        try {
            startActivity(alarmIntent);
            Toast.makeText(this,
                    String.format("%02d:%02d에 알람이 설정되었습니다", hour, minute),
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "알람 설정 중 오류가 발생했습니다: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 뒤로가기 애니메이션 설정
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}