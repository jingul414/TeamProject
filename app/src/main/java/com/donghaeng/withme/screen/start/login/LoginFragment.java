package com.donghaeng.withme.screen.start.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.Login;
import com.donghaeng.withme.screen.start.StartActivity;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends Fragment {

    Button login_btn;
    StartActivity startActivity;
    TextView testTextView;
    Login loginAuth;
    EditText phoneNumEdit;
    EditText passwdEdit;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_btn = view.findViewById(R.id.login_button);
        startActivity = (StartActivity) requireActivity();
        phoneNumEdit = view.findViewById(R.id.login_text_phone_number);
        passwdEdit = view.findViewById(R.id.login_text_password);

        login_btn.setOnClickListener(v -> {
            // 로그인 검증 로직 추가
            String phoneNumStr = phoneNumEdit.getText().toString();
            String passwdStr = passwdEdit.getText().toString();
            loginAuth = new Login(phoneNumStr);
            loginAuth.verifyUser(passwdStr, result -> {
                if (result) {
                    // 로그인 성공
                    Log.e("LoginFragment", "로그인 성공");
                    startActivity.changeFragment("controller");
                } else {
                    // 로그인 실패
                    Log.e("LoginFragment", "로그인 실패");
                }
            });
        });
        return view;
    }

}