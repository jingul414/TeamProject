package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;

// 회원가입 액티비티
public class SignUpActivity extends AppCompatActivity {

    EditText id_editText;
    EditText pw_editText;
    EditText pw_check_editText;
    Button sign_up_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            id_editText = (EditText) findViewById(R.id.id_editText);
            pw_editText = (EditText) findViewById(R.id.pw_editText);
            pw_check_editText = (EditText) findViewById(R.id.pw_check_editText);
            sign_up_btn = (Button) findViewById(R.id.sign_up_btn);
            sign_up_btn.setOnClickListener(new SingUpBtnListener());

            return insets;
        });
    }
    class SingUpBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String id = String.valueOf(id_editText.getText());
            String pw = String.valueOf(pw_editText.getText());
            String pw_check = String.valueOf(pw_check_editText.getText());

            if(id.isEmpty()){
                Toast.makeText(SignUpActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (pw.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (pw_check.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!pw.equals(pw_check)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Intent sign_intent = new Intent(SignUpActivity.this, LoginActivity.class);
                Toast.makeText(SignUpActivity.this, "회원가입 성공.", Toast.LENGTH_SHORT).show();
                startActivity(sign_intent);
            }
        }
    }
}