package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.SignUp;
import com.donghaeng.withme.screen.start.StartActivity;

public class SignupNameFragment extends Fragment {

    StartActivity startActivity;

    TextView nameNotificationText;
    EditText nameEdit;
    Button nextBtn;

    public SignupNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_name, container, false);

        nameNotificationText = view.findViewById(R.id.signup_text_name_notification);
        nameEdit = view.findViewById(R.id.signup_edit_name);
        nextBtn = view.findViewById(R.id.signup_btn_next);
        startActivity = (StartActivity) requireActivity();
        nextBtn.setOnClickListener(new NextBtnListener());

        // 회원가입 인스턴스 생성
        SignUp signUp = new SignUp(startActivity);
        startActivity.setSignUpInstance(signUp);
        return view;
    }

    class NextBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity.getSignUpInstance().storeName(nameEdit);
        }
    }
}