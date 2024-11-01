package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.donghaeng.withme.R;

public class StartActivity extends AppCompatActivity {
    private String sign_name = "";
    private String sign_number = "";
    private String sign_pw = "";
    private String sign_pw_valid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        // Fragment 초기화 로직을 분리
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new StartFragment())
                    .commit();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void changeFragment(String fragmentName){
        // 액티비티가 유효한 상태인지 확인
        if (!isFinishing() && !isDestroyed()) {
            Intent intent = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (fragmentName) {
                case "controller":
                    intent = new Intent(this, ControllerActivity.class);
                    startActivity(intent);
                    // 선택적: 현재 액티비티 종료
                    // finish();
                    break;
                case "login":
                    transaction.replace(R.id.fragment_container, new LoginFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "signUp":  // StartFragment.java에서 "signUp"으로 호출했으므로 케이스도 "signUp"으로 수정
                    transaction.replace(R.id.fragment_container, new SignupStep1Fragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "step2":
                    transaction.replace(R.id.fragment_container, new SignupStep2Fragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "step3":
                    transaction.replace(R.id.fragment_container, new SignupStep3Fragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                default:
                    break;
            }
        }

    }
    void setSignName(String name){
        sign_name = name;
    }
    void setSignNumber(String number){
        sign_number = number;
    }
    void setSignPw(String pw){
        sign_pw = pw;
    }
    void setSignPwValid(String pw_valid){
        sign_pw_valid = pw_valid;
    }
    String getSignName(){
        return sign_name;
    }
    String getSignNumber(){
        return sign_number;
    }
    String getSignPw(){
        return sign_pw;
    }
    String getSignPwValid(){
        return sign_pw_valid;
    }
}