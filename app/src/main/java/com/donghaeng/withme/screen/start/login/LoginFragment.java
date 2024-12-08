package com.donghaeng.withme.screen.start.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.donghaeng.withme.login.Login;
import com.donghaeng.withme.screen.start.StartActivity;

import org.mindrot.jbcrypt.BCrypt;

public class LoginFragment extends Fragment {

    Button login_btn;
    StartActivity startActivity;
    TextView testTextView;
    Login loginAuth;
    EditText phoneNumEdit;
    EditText passwdEdit;
    CheckBox autoLoginCheck;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AutomaticLoginChecker.performLoginIfEnabled(
                this.requireContext(),
                () -> {
                    User user = AutomaticLoginChecker.getUser();
                    if (user != null) {
                        ((StartActivity) requireActivity()).setUser(user);
                        switch (user.getUserType()) {
                            case UserType.CONTROLLER:
                                ((StartActivity) requireActivity()).changeFragment("controller");
                                break;
                            case UserType.TARGET:
                                ((StartActivity) requireActivity()).changeFragment("target");
                                break;
                            case UserType.UNDEFINED:
                                ((StartActivity) requireActivity()).changeFragment("SelectFragment");
                                break;
                            default:
                                Log.e("LoginFragment", "알 수 없는 유저 타입");
                                break;
                        }
                    } else {
                        Log.e("LoginFragment", "자동 로그인 User 객체가 null입니다.");
                    }
                },
                () -> {
                    Log.e("LoginFragment", "자동 로그인 실패 또는 비활성화 상태.");
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_btn = view.findViewById(R.id.login_button);
        startActivity = (StartActivity) requireActivity();
        phoneNumEdit = view.findViewById(R.id.login_text_phone_number);
        passwdEdit = view.findViewById(R.id.login_text_password);
        autoLoginCheck = view.findViewById(R.id.login_radio_login_history);

        login_btn.setOnClickListener(v -> {
            // 로그인 검증 로직 추가
            String phoneNumStr = phoneNumEdit.getText().toString();
            String passwdStr = passwdEdit.getText().toString();
            loginAuth = new Login(this, phoneNumStr);
            loginAuth.verifyUser(passwdStr, result -> {
                if (result) {
                    // 로그인 성공
                    Log.e("LoginFragment", "로그인 성공");
                } else {
                    // 로그인 실패
                    Toast.makeText(getActivity(), "로그인 실패", Toast.LENGTH_LONG).show();
                    Log.e("LoginFragment", "로그인 실패");
                }
            });
        });
        phoneNumEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        return view;
    }

    public CheckBox getCheckBox(){
        return autoLoginCheck;
    }
}