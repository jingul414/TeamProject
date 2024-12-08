package com.donghaeng.withme.screen.start;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.donghaeng.withme.R;

public class StartFragment extends Fragment {

    StartActivity startActivity;

    LinearLayout signupBtn;
    LinearLayout loginBtn;

    private long backPressedTime = 0;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 뒤로가기 처리
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finishAffinity();
                    return;
                }
                Toast.makeText(requireContext(), "뒤로가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                backPressedTime = System.currentTimeMillis();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);
        signupBtn = view.findViewById(R.id.signup_button);
        loginBtn = view.findViewById(R.id.login_button);

        startActivity = (StartActivity) requireActivity();

        signupBtn.setOnClickListener(v -> startActivity.changeFragment("SignupNameFragment"));
        loginBtn.setOnClickListener(v -> startActivity.changeFragment("LoginFragment"));

        return view;
    }
}