package com.donghaeng.withme.screen.start.signup;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.donghaeng.withme.screen.start.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupStep2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupStep2Fragment extends Fragment {

    private static final String TAG = "PhoneAuthFragment";

    StartActivity startActivity;

    TextView phoneNumbernotificationTextView;
    EditText phoneNumberEditText;
    Button sendAuthenticationNumberButton;
    TextView authenticationNumberNotificationTextView;
    EditText authenticationNumberEditText;
    Button nextButton;
    TextView notReceiveAuthenticationNumberTextView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PHONE_NUMBER = "Phone Number";
    private static final String ARG_AUTHENTICATION_NUMBER = "Authentication Number";

    // TODO: Rename and change types of parameters
    private String phoneNumber;
    private String authenticationNumber;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public SignupStep2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupStep2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupStep2Fragment newInstance(String param1, String param2) {
        SignupStep2Fragment fragment = new SignupStep2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, param1);
        args.putString(ARG_AUTHENTICATION_NUMBER, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(ARG_PHONE_NUMBER);
            authenticationNumber = getArguments().getString(ARG_AUTHENTICATION_NUMBER);
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                /*
                 * 이 콜백이 호출되는 경우
                 * 1. 경우에 따라 인증 코드를 보내거나 입력할 필요 없이 전화번호를 즉시 인증하는 경우
                 * 2. Google Play 서비스가 수신되는 인증 SSMS 자동으로 감지하고 사용자의 조치 없이도 인증을 수행하는 경우
                 */
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                /*
                 *  전화번호 형식이 유효하지 않은 경우 등 유효하지 않은 인증 요청일 경우 호출
                 */
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // 유효하지 않은 요청
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // 프로젝트의 SMS 할달량 초과
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // null Activity reCAPTCHA 검증 시도
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                }
                // 메시지 표시 및 UI 업데이트
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                /*
                 * 제공된 전화번호로 SMS 인증 코드가 전송되었으므로 이제 사용자에게 코드를
                 * 입력하도록 요청한 다음 코드를 인증 ID와 결합하여 자격 증명을 구성
                 */
                Log.d(TAG, "onCodeSent:" + verificationId);

                // 나중에 사용할 수 있도록 인증 ID를 저장하고 토큰을 다시 보내기
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_step2, container, false);
        // UI 요소 초기화
        phoneNumbernotificationTextView = view.findViewById(R.id.signup_text_name);
        phoneNumberEditText = view.findViewById(R.id.edit_phone_number);
        sendAuthenticationNumberButton = view.findViewById(R.id.btn_send_authentication_number);
        authenticationNumberNotificationTextView = view.findViewById(R.id.signup_certification);
        authenticationNumberEditText = view.findViewById(R.id.edit_authentication_number);
        nextButton = view.findViewById(R.id.btn_next);
        notReceiveAuthenticationNumberTextView = view.findViewById(R.id.not_receive_certification);
        authenticationNumberNotificationTextView.setVisibility(View.INVISIBLE);
        authenticationNumberEditText.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        notReceiveAuthenticationNumberTextView.setVisibility(View.INVISIBLE);
        startActivity = (StartActivity) requireActivity();
        sendAuthenticationNumberButton.setOnClickListener(new SendAuthenticationNumberBtnListener());
        nextButton.setOnClickListener(new NextBtnListener());
        notReceiveAuthenticationNumberTextView.setOnClickListener(new NotReceiveAuthenticationNumberListener());
        return view;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(startActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        startActivity.changeFragment("step3");
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Log.e(TAG, "signInWithCredential:failure: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(startActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    class SendAuthenticationNumberBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            phoneNumber = String.valueOf(phoneNumberEditText.getText());

            if (phoneNumber.isEmpty()) {
                Toast.makeText(startActivity, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!phoneNumber.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) {
                Toast.makeText(startActivity, "전화번호의 형식이 아닙니다.", Toast.LENGTH_LONG).show();
            }else{
                String phone = phoneNumber;
                phone = phone.replace("-", "");
                phone = "+82" + phone.substring(1);

                // 전화번호 인증
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phone)       // 인증할 핸드폰 번호
                                .setTimeout(60L, TimeUnit.SECONDS) // 시간 초과 기준 및 단위
                                .setActivity(startActivity)                 // (optional) Activity for callback binding
                                // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                Log.w(TAG, "전화번호: " + phone);

                authenticationNumberNotificationTextView.setVisibility(View.VISIBLE);
                authenticationNumberEditText.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                notReceiveAuthenticationNumberTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    class NextBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            authenticationNumber = String.valueOf(authenticationNumberEditText.getText());
            if (authenticationNumber.isEmpty()) {
                Toast.makeText(startActivity, "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (authenticationNumber.length() != 6) {
                Toast.makeText(startActivity, "올바르지 않은 인증번호 양식입니다.", Toast.LENGTH_LONG).show();
            } else{
                verifyPhoneNumberWithCode(mVerificationId, authenticationNumber);
            }
        }
    }

    class NotReceiveAuthenticationNumberListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String phone = phoneNumber;
            phone = phone.replace("-", "");
            phone = "+82" + phone.substring(1);
            resendVerificationCode(phone, mResendToken);
        }
    }
}