package com.donghaeng.withme.screen.setting;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.donghaeng.withme.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new SettingFragment())
                .commit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 뒤로가기 애니메이션 설정
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void changeFragment(String fragmentName){
        // 액티비티가 유효한 상태인지 확인
        if (!isFinishing() && !isDestroyed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations( // 프래그먼트 전환 애니메이션 설정
                    R.anim.slide_in_right,  // 새로운 프래그먼트 들어올 때
                    R.anim.slide_out_left,  // 현재 프래그먼트 나갈 때
                    R.anim.slide_in_left,   // 뒤로가기할 때 새 프래그먼트 들어올 때
                    R.anim.slide_out_right  // 뒤로가기할 때 현재 프래그먼트 나갈 때
            );

            // TODO 프래그먼트 추가 후 수정 예정
            switch (fragmentName) {
                case "ChangeNumber":
//                    transaction.replace(R.id.fragment_container, new LoginFragment());
//                    transaction.addToBackStack(null); // 뒤로가기 지원
//                    transaction.commit();
                    break;
                case "ChangePW":
//                    transaction.replace(R.id.fragment_container, new SignupNameFragment());
//                    transaction.addToBackStack(null); // 뒤로가기 지원
//                    transaction.commit();
                    break;
                case "PermitList":
//                    transaction.replace(R.id.fragment_container, new SignupNameFragment());
//                    transaction.addToBackStack(null); // 뒤로가기 지원
//                    transaction.commit();
                    break;
                case "PermitListControl":
                    transaction.replace(R.id.fragment_container, new FragmentControllerOpt());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                case "PermitListTarget":
                    transaction.replace(R.id.fragment_container, new FragmentTargetOpt());
                    transaction.addToBackStack(null); // 뒤로가기 지원
                    transaction.commit();
                    break;
                default:
                    break;
            }
        }

    }
}