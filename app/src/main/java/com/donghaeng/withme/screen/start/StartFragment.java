package com.donghaeng.withme.screen.start;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.donghaeng.withme.R;

public class StartFragment extends Fragment {

    StartActivity startActivity;

    LinearLayout signupBtn;
    LinearLayout loginBtn;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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