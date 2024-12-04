package com.donghaeng.withme.screen.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.Login;
import com.donghaeng.withme.login.SignUp;
import com.donghaeng.withme.screen.guide.GuideActivity;
import com.donghaeng.withme.screen.start.connect.SelectFragment;
import com.donghaeng.withme.screen.start.login.LoginFragment;
import com.donghaeng.withme.screen.main.ControllerActivity;
import com.donghaeng.withme.screen.main.TargetActivity;
import com.donghaeng.withme.screen.start.signup.SignupNameFragment;
import com.donghaeng.withme.screen.start.signup.SignupVerifyingPhoneNumberFragment;
import com.donghaeng.withme.screen.start.signup.SignupPassWordFragment;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.Undefined;
import com.donghaeng.withme.data.user.User;

public class StartActivity extends AppCompatActivity {
    // 회원가입, 로그인 객체
    private SignUp signUp;
    private Login login;
    private User user;

    // 화면 이동용 임시 버튼
    Button guide_btn, control_btn, target_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        // 화면 이동용 임시 버튼 초기화
        guide_btn = findViewById(R.id.guide_button);
        control_btn = findViewById(R.id.control_button);
        target_btn = findViewById(R.id.target_button);
        // 화면 이동용 임시 버튼 클릭 리스너 설정
        guide_btn.setOnClickListener(v -> startActivity(new Intent(this, GuideActivity.class)));
        control_btn.setOnClickListener(v -> changeFragment("controller"));
        target_btn.setOnClickListener(v -> changeFragment("target"));

        // Fragment 초기화 로직을 분리
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new StartFragment())
                    .commit();
            if (getIntent().getStringExtra("fragmentName") != null) {
                changeFragment(getIntent().getStringExtra("fragmentName"));
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void changeFragment(String fragmentName) {
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
                    intent.putExtra("user", (Parcelable) user);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    // 선택적: 현재 액티비티 종료
                    // finish();
                    break;
                case "target":
                    intent = new Intent(this, TargetActivity.class);
                    intent.putExtra("user", (Parcelable) user);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case "LoginFragment":
                    transaction.replace(R.id.fragment_container, new LoginFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "SignupNameFragment":  // StartFragment.java에서 "signUp"으로 호출했으므로 케이스도 "signUp"으로 수정
                    transaction.replace(R.id.fragment_container, new SignupNameFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "SignupVerifyingPhoneNumberFragment":
                    transaction.replace(R.id.fragment_container, new SignupVerifyingPhoneNumberFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "SignupPassWordFragment":
                    transaction.replace(R.id.fragment_container, new SignupPassWordFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "SelectFragment":
                    transaction.replace(R.id.fragment_container, new SelectFragment());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "controller_QR":
                    intent = new Intent(this, ControllerActivity.class);
                    intent.putExtra("user", (Parcelable) user);
                    intent.putExtra("fragmentName", "controller_QR");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case "target_QR":
                    intent = new Intent(this, TargetActivity.class);
                    intent.putExtra("user", (Parcelable) user);
                    intent.putExtra("fragmentName", "target_QR");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                default:
                    break;
            }
        }

    }

    public void setSignUpInstance(SignUp signUp) {
        this.signUp = signUp;
    }

    public void setLoginInstance(Login login) {
        this.login = login;
    }

    public SignUp getSignUpInstance() {
        return this.signUp;
    }

    public Login getLoginInstance() {
        return this.login;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 뒤로가기 애니메이션 설정
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}