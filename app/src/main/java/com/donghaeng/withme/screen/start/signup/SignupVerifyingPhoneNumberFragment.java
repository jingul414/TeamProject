package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

    private final Handler handler = new Handler();
    private Runnable showNotReceiveCodeTextRunnable;

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
        phoneNumberNotificationText         = view.findViewById(R.id.signup_text_phone_number_notification1);
        phoneNumberEdit                     = view.findViewById(R.id.signup_edit_phone_number);
        sendingVerificationCodeBtn          = view.findViewById(R.id.signup_btn_send_verification_code);
        verificationCodeNotificationText    = view.findViewById(R.id.signup_verification_code);
        verificationCodeEdit                = view.findViewById(R.id.signup_edit_verification_code);
        nextBtn                             = view.findViewById(R.id.btn_next);
        notReceiveVerificationCodeText      = view.findViewById(R.id.signup_not_receive_verification_code);

        // 초기 상태로 숨김
        verificationCodeNotificationText.setVisibility(View.INVISIBLE);
        verificationCodeEdit.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        notReceiveVerificationCodeText.setVisibility(View.INVISIBLE);

        startActivity = (StartActivity) requireActivity();
        phoneNumberEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        sendingVerificationCodeBtn.setOnClickListener(new SendingVerificationCodeBtnListener());
        nextBtn.setOnClickListener(new NextBtnListener());
        notReceiveVerificationCodeText.setOnClickListener(new NotReceiveVerificationCodeTextListener());

        startActivity.getSignUpInstance().initializePhoneAuth(verificationCodeNotificationText, verificationCodeEdit, nextBtn);
        return view;
    }

    class SendingVerificationCodeBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try{
                startActivity.getSignUpInstance().sendVerificationCode(phoneNumberEdit);

//                // UI 요소 표시
//                verificationCodeNotificationText.setVisibility(View.VISIBLE);
//                verificationCodeEdit.setVisibility(View.VISIBLE);
//                nextBtn.setVisibility(View.VISIBLE);

                // "인증번호가 오지 않아요" 메시지를 타이머를 통해 일정 시간 후 표시
                startNotReceiveCodeTimer();
            } catch (EmptyPhoneNumberException e){
                Toast.makeText(startActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (InvalidPhoneNumberException e){
                Toast.makeText(startActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Verification failed: " + e.getMessage());
            }
        }
    }

    private void startNotReceiveCodeTimer() {
        // 이전 Runnable이 있다면 제거
        if (showNotReceiveCodeTextRunnable != null) {
            handler.removeCallbacks(showNotReceiveCodeTextRunnable);
        }

        // 새로운 Runnable 정의
        showNotReceiveCodeTextRunnable = () -> notReceiveVerificationCodeText.setVisibility(View.VISIBLE);

        // 10초 후 실행 (필요시 시간 조정 가능)
        handler.postDelayed(showNotReceiveCodeTextRunnable, 10000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment가 파괴될 때 타이머 콜백 제거
        if (showNotReceiveCodeTextRunnable != null) {
            handler.removeCallbacks(showNotReceiveCodeTextRunnable);
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