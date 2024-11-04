package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.start.StartActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupStep3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupStep3Fragment extends Fragment {
    StartActivity startActivity;
    Button next_button;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupStep3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupStep3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupStep3Fragment newInstance(String param1, String param2) {
        SignupStep3Fragment fragment = new SignupStep3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_step3, container, false);
        next_button = view.findViewById(R.id.btn_next);
        startActivity = (StartActivity) requireActivity();
        next_button.setOnClickListener(v -> {
            EditText editText1 = view.findViewById(R.id.input_password);
            EditText editText2 = view.findViewById(R.id.input_password_valid);
            String sign_pw = editText1.getText().toString();
            String sign_pw_valid = editText2.getText().toString();

            startActivity.setSignPw(sign_pw);
            startActivity.setSignPwValid(sign_pw_valid);
            startActivity.changeFragment("select");
        });
        return view;
    }
}