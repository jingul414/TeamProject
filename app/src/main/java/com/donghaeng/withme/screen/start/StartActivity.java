package com.donghaeng.withme.screen.start;

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
import com.donghaeng.withme.screen.start.connect.SelectFragment;
import com.donghaeng.withme.screen.start.login.LoginFragment;
import com.donghaeng.withme.screen.main.ControllerActivity;
import com.donghaeng.withme.screen.main.TargetActivity;
import com.donghaeng.withme.screen.start.signup.SignupStep1Fragment;
import com.donghaeng.withme.screen.start.signup.SignupStep2Fragment;
import com.donghaeng.withme.screen.start.signup.SignupStep3Fragment;

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

    public void changeFragment(String fragmentName){
        // 액티비티가 유효한 상태인지 확인
        if (!isFinishing() && !isDestroyed()) {
            Intent intent = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations( // 프래그먼트 전환 애니메이션 설정
                    R.anim.slide_in_right,  // 새로운 프래그먼트 들어올 때
                    R.anim.slide_out_left,  // 현재 프래그먼트 나갈 때
                    R.anim.slide_in_left,   // 뒤로가기할 때 새 프래그먼트 들어올 때
                    R.anim.slide_out_right  // 뒤로가기할 때 현재 프래그먼트 나갈 때
            );

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
                case "select":
                    transaction.replace(R.id.fragment_container, new SelectFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "controller_QR":
                    intent = new Intent(this, ControllerActivity.class);
                    intent.putExtra("fragmentName", "controller_QR");
                    startActivity(intent);
                    break;
                case "target_QR":
                    intent = new Intent(this, TargetActivity.class);
                    intent.putExtra("fragmentName", "target_QR");
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    }
    public void setSignName(String name){
        sign_name = name;
    }
    public void setSignNumber(String number){
        sign_number = number;
    }
    public void setSignPw(String pw){
        sign_pw = pw;
    }
    public void setSignPwValid(String pw_valid){
        sign_pw_valid = pw_valid;
    }
    public String getSignName(){
        return sign_name;
    }
    public String getSignNumber(){
        return sign_number;
    }
    public String getSignPw(){
        return sign_pw;
    }
    public String getSignPwValid(){
        return sign_pw_valid;
    }
}