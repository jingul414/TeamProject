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
import com.donghaeng.withme.screen.start.StartActivity;

import java.util.Timer;
import java.util.TimerTask;
import org.mindrot.jbcrypt.BCrypt;

public class SignupPassWordFragment extends Fragment {

    StartActivity startActivity;

    TextView pwNotificationText;
    EditText pwEdit;
    EditText pwCheckEdit;
    TextView warningText;
    Button nextBtn;

    private Timer timer;
    private TimerTask hideWarningTask;

    public SignupPassWordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = new Timer();
        hideWarningTask = new TimerTask() {
            @Override
            public void run() {
                if (warningText.getVisibility() == View.VISIBLE) warningText.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_password, container, false);

        pwNotificationText = view.findViewById(R.id.signup_text_password_notification);
        pwEdit = view.findViewById(R.id.signup_edit_password);
        pwCheckEdit = view.findViewById(R.id.signup_edit_password_check);
        warningText = view.findViewById(R.id.signup_text_warning);
        nextBtn = view.findViewById(R.id.signup_btn_next);
        startActivity = (StartActivity) requireActivity();
        warningText.setVisibility(View.INVISIBLE);
        nextBtn.setOnClickListener(new NextBtnListener());
        return view;
    }

    class NextBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String pw = pwEdit.getText().toString();
            String pwCheck = pwCheckEdit.getText().toString();

            if (pw.isEmpty()) {
                Toast.makeText(startActivity, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if(pw.length() < 8){
                Toast.makeText(startActivity, "비밀번호를 8자 이상 입력하세요.", Toast.LENGTH_LONG).show();
            }else if (pwCheck.isEmpty()) {
                Toast.makeText(startActivity, "비밀번호 확인(재입력)을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!pw.equals(pwCheck)) {
                warningText.setVisibility(View.VISIBLE);
                timer.schedule(hideWarningTask, 5000);
            } else {
                String hashedPassWord = BCrypt.hashpw(pw, BCrypt.gensalt()); // 비밀번호를 해시하여 저장
                startActivity.setUserHashedPassWord(hashedPassWord);
                startActivity.changeFragment("SelectFragment");
            }
        }
    }
}