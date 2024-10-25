package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.login.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import com.donghaeng.withme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// 로그인 액티비티
public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    TextView sign_text;
    EditText id_edit;
    EditText pw_edit;
    CheckBox re_id_check;
    CheckBox auto_login_check;

    // database 연결
    private FirebaseFirestore db;
    // 인증 연결
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // 자동 로그인 시 사용
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 모든 firebase 기능 초기화
        db = FirebaseFirestore.getInstance();
        Login login = new Login(db);
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            login_btn = (Button) findViewById(R.id.login_btn);
            sign_text = (TextView) findViewById(R.id.sign_up_text);
            id_edit = (EditText) findViewById(R.id.phone_editText);
            pw_edit = (EditText) findViewById(R.id.PW_editText);
            re_id_check = (CheckBox) findViewById(R.id.re_id_checkBox);
            auto_login_check = (CheckBox) findViewById(R.id.auto_login_checkBox);

            login_btn.setOnClickListener(new LoginBtnListener());
            sign_text.setOnClickListener(new SignUpTextListener());

            //  네비게이션 설정
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent selectedIntent = null;
                    System.out.println(item.getItemId());

                    if (item.getItemId() == R.id.nav_controller) {
                        selectedIntent = new Intent(LoginActivity.this, ControllerMainActivity.class);
                    } else if (item.getItemId() == R.id.nav_target) {
                        selectedIntent = new Intent(LoginActivity.this, TargetMainActivity.class);
                    }
                    if(selectedIntent != null) startActivity(selectedIntent);

                    return true;
                }
            });


            return insets;
        });
    }

    class LoginBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String id, pw;
            id = String.valueOf(id_edit.getText());
            pw = String.valueOf(pw_edit.getText());
            Boolean remember_id = re_id_check.isChecked();
            Boolean auto_login = auto_login_check.isChecked();

            if(id.isEmpty()){
                Toast.makeText(LoginActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (pw.isEmpty()) {
                Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                Intent select_intent = new Intent(LoginActivity.this, SelectControlActivity.class);
                startActivity(select_intent);
            }

//            로그인 시 데이터 넘기기 테스트
//            Intent login_intent = new Intent(LoginActivity.this, SelectControlActivitiy.class);
//            login_intent.putExtra("ID", id);
//            login_intent.putExtra("PW", pw);
//            login_intent.putExtra("RE_ID", remember_id);
//            login_intent.putExtra("AUTO_LOGIN", auto_login);
//            startActivity(login_intent);

        }
    }

    class SignUpTextListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent sign_up_intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(sign_up_intent);
        }
    }
}