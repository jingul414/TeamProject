package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;

// 제어자 피제어자 선택 액티비티
public class SelectControlActivity extends AppCompatActivity {

    Button controller_btn;
    Button target_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_control);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // 로그인 시 정보 받기 테스트
//            TextView tv = (TextView) findViewById(R.id.textView);
//
//            Intent login_intent = getIntent();
//            String id = login_intent.getStringExtra("ID");
//            String pw = login_intent.getStringExtra("PW");
//            Boolean re_id = login_intent.getBooleanExtra("RE_ID", false);
//            Boolean auto_login = login_intent.getBooleanExtra("AUTO_LOGIN", false);
//
//            tv.setText("아이디 : " + id + "\n비밀번호 : " + pw + "\n아이디 기억 여부 : " + re_id + "\n 자동 로그인 여부 : " + auto_login);

            controller_btn = (Button) findViewById(R.id.controller_btn);
            target_btn = (Button) findViewById(R.id.target_btn);

            controller_btn.setOnClickListener(new ControllerBtnListener());
            target_btn.setOnClickListener(new TargetBtnListener());

            return insets;
        });
    }

    class ControllerBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent control_connect_intent = new Intent(SelectControlActivity.this, ControllerConnectActivity.class);
            startActivity(control_connect_intent);
        }
    }

    class TargetBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent target_connect_intent = new Intent(SelectControlActivity.this, TargetConnectActivity.class);
            startActivity(target_connect_intent);
        }
    }

}