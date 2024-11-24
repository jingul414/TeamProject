package com.donghaeng.withme.screen.start.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.screen.start.signup.SignUpFragment;
import com.donghaeng.withme.screen.start.signup.SignupNameFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LoginFragment extends Fragment {

    Button login_btn;
    StartActivity startActivity;
    TextView testTextView;

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
        login_btn.setOnClickListener(v -> {
            // 로그인 검증 로직 추가
            startActivity.changeFragment("controller");
        });

        // '비밀번호를 정하지 않았어요' 문구 클릭 시 회원가입 화면으로 이동
        TextView setPasswordText = view.findViewById(R.id.login_text_set_password);
        setPasswordText.setOnClickListener(v -> {
            // SignupFragment로 이동
            startActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right) // 애니메이션 추가. GPT가 알려줘서 걍 해봄.
                    .replace(R.id.fragment_container, new SignupNameFragment())
                    .addToBackStack(null) // 뒤로가기 기능 추가.
                    .commit();
        });

        return view;
    }

}