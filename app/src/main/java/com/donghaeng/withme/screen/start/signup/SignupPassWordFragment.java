package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.start.StartActivity;

public class SignupPassWordFragment extends Fragment {

    StartActivity startActivity;

    TextView pwNotificationText;
    EditText pwEdit;
    EditText pwCheckEdit;
    TextView warningText;
    Button nextBtn;

    public SignupPassWordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_password, container, false);

        // TODO: 여기도
        pwNotificationText = view.findViewById(R.id.signup_text_password_notification1);
        pwEdit = view.findViewById(R.id.signup_edit_password);
        pwCheckEdit = view.findViewById(R.id.signup_edit_password_check);
        warningText = view.findViewById(R.id.signup_text_warning);
        nextBtn = view.findViewById(R.id.signup_btn_next);
        startActivity = (StartActivity) requireActivity();
        warningText.setVisibility(View.INVISIBLE);
        nextBtn.setOnClickListener(new NextBtnListener());
        startActivity.getSignUpInstance().makeAlertTask(warningText);
        return view;
    }

    class NextBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity.getSignUpInstance().checkPassWord(pwEdit, pwCheckEdit, warningText);
        }
    }
}