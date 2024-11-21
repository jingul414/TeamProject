package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.exception.phonenumber.EmptyPhoneNumberException;
import com.donghaeng.withme.exception.phonenumber.InvalidPhoneNumberException;
import com.donghaeng.withme.screen.start.StartActivity;

public class SignupVerifyingPhoneNumberFragment extends Fragment {

    private static final String TAG = "SignupVerifyingPhoneNumberFragment";

    private StartActivity startActivity;

    TextView phoneNumberNotificationText;
    EditText phoneNumberEdit;
    Button sendingVerificationCodeBtn;
    TextView verificationCodeNotificationText;
    EditText verificationCodeEdit;
    Button nextBtn;
    TextView notReceiveVerificationCodeText;

    public SignupVerifyingPhoneNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_verifying_phone_number, container, false);
        // UI 요소 초기화
        // TODO: 안내 문구를 두 줄로 나눠쓰면서 아이디가 변경되었는데 UI 요소 초기화에 뭘 넣어야하지..? 이게 머야
        phoneNumberNotificationText = view.findViewById(R.id.signup_text_phone_number_notification1);
        phoneNumberEdit = view.findViewById(R.id.signup_edit_phone_number);
        sendingVerificationCodeBtn = view.findViewById(R.id.signup_btn_send_verification_code);
        verificationCodeNotificationText = view.findViewById(R.id.signup_verification_code);
        verificationCodeEdit = view.findViewById(R.id.signup_edit_verification_code);
        nextBtn = view.findViewById(R.id.btn_next);
        notReceiveVerificationCodeText = view.findViewById(R.id.signup_not_receive_verification_code);
        verificationCodeNotificationText.setVisibility(View.INVISIBLE);
        verificationCodeEdit.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        notReceiveVerificationCodeText.setVisibility(View.INVISIBLE);
        startActivity = (StartActivity) requireActivity();
        sendingVerificationCodeBtn.setOnClickListener(new SendingVerificationCodeBtnListener());
        nextBtn.setOnClickListener(new NextBtnListener());
        notReceiveVerificationCodeText.setOnClickListener(new NotReceiveVerificationCodeTextListener());

        startActivity.getSignUpInstance().initializePhoneAuth();
        return view;
    }

    class SendingVerificationCodeBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try{
                startActivity.getSignUpInstance().sendVerificationCode(phoneNumberEdit);

                verificationCodeNotificationText.setVisibility(View.VISIBLE);
                verificationCodeEdit.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                notReceiveVerificationCodeText.setVisibility(View.VISIBLE);
            } catch (EmptyPhoneNumberException e){
                Toast.makeText(startActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (InvalidPhoneNumberException e){
                Toast.makeText(startActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Verification failed: " + e.getMessage());
            }
        }
    }

    class NextBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity.getSignUpInstance().checkVerificationCode(verificationCodeEdit);
        }
    }

    class NotReceiveVerificationCodeTextListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity.getSignUpInstance().resendVerificationCode();
        }
    }
}