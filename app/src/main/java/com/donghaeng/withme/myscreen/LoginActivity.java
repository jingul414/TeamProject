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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;

// 로그인 액티비티
public class LoginActivity extends AppCompatActivity {

    Button login_btn;
    TextView sign_text;
    EditText id_edit;
    EditText pw_edit;
    CheckBox re_id_check;
    CheckBox auto_login_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            login_btn = (Button) findViewById(R.id.login_btn);
            sign_text = (TextView) findViewById(R.id.sign_up_text);
            id_edit = (EditText) findViewById(R.id.id_editText);
            pw_edit = (EditText) findViewById(R.id.PW_editText);
            re_id_check = (CheckBox) findViewById(R.id.re_id_checkBox);
            auto_login_check = (CheckBox) findViewById(R.id.auto_login_checkBox);

            login_btn.setOnClickListener(new LoginBtnListener());
            sign_text.setOnClickListener(new SignUpTextListener());

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