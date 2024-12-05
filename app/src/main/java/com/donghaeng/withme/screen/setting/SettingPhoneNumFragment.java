package com.donghaeng.withme.screen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.exception.phonenumber.EmptyPhoneNumberException;
import com.donghaeng.withme.exception.phonenumber.InvalidPhoneNumberException;
import com.donghaeng.withme.screen.start.StartActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SettingPhoneNumFragment extends Fragment {
    private static final String TAG = "SettingPhoneNumFragment";
    private User user;
    private EditText phoneNumberEdit;
    private EditText verificationCodeEdit;
    private Button sendVerificationButton;
    private Button nextButton;
    private TextView verificationCodeNotification;
    private TextView notReceiveCodeText;
    private FireStoreManager firestoreManager;
    private final Handler handler = new Handler();
    private Runnable showNotReceiveCodeRunnable;

    // Firebase Phone Auth 관련
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber;
    private FirebaseUser mFirebaseUser;

    public SettingPhoneNumFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_phone_num, container, false);

        initializeViews(view);
        initializePhoneAuth();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        phoneNumberEdit = view.findViewById(R.id.signup_edit_phone_number);
        verificationCodeEdit = view.findViewById(R.id.signup_edit_verification_code);
        sendVerificationButton = view.findViewById(R.id.signup_btn_send_verification_code);
        nextButton = view.findViewById(R.id.btn_next);
        verificationCodeNotification = view.findViewById(R.id.signup_verification_code);
        notReceiveCodeText = view.findViewById(R.id.signup_not_receive_verification_code);
        firestoreManager = FireStoreManager.getInstance();

        // 초기 상태로 숨김
        verificationCodeNotification.setVisibility(View.INVISIBLE);
        verificationCodeEdit.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        notReceiveCodeText.setVisibility(View.INVISIBLE);
    }

    private void initializePhoneAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void setupListeners() {
        sendVerificationButton.setOnClickListener(v -> {
            try {
                sendVerificationCode();
            } catch (EmptyPhoneNumberException | InvalidPhoneNumberException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        nextButton.setOnClickListener(v -> checkVerificationCode());
        notReceiveCodeText.setOnClickListener(v -> resendVerificationCode());
    }

    private void sendVerificationCode() throws EmptyPhoneNumberException, InvalidPhoneNumberException {
        phoneNumber = phoneNumberEdit.getText().toString();

        if (phoneNumber.isEmpty()) {
            throw new EmptyPhoneNumberException("전화번호를 입력하세요.");
        } else if (!phoneNumber.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) {
            throw new InvalidPhoneNumberException("전화번호의 형식이 아닙니다.");
        }

        changeToAvailablePhoneNumber();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.w(TAG, "전화번호: " + phoneNumber);

        // UI 요소 표시
        verificationCodeNotification.setVisibility(View.VISIBLE);
        verificationCodeEdit.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        startNotReceiveCodeTimer();
    }

    private void checkVerificationCode() {
        String code = verificationCodeEdit.getText().toString();
        if (code.isEmpty()) {
            Toast.makeText(getContext(), "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (code.length() != 6) {
            Toast.makeText(getContext(), "올바르지 않은 인증번호 양식입니다.", Toast.LENGTH_LONG).show();
        } else {
            verifyPhoneNumberWithCode(mVerificationId, code);
        }
    }

    private void changeToAvailablePhoneNumber() {
        if (!phoneNumber.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) return;
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = "+82" + phoneNumber.substring(1);
    }

    private void storePhoneNumber() {
        phoneNumber = "0" + phoneNumber.substring(3);
    }

    private void resendVerificationCode() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(mResendToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Log.e(TAG, "Verification failed: " + e.getMessage());
            Toast.makeText(getContext(), "인증에 실패했습니다. 올바른 인증번호를 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        mFirebaseUser = task.getResult().getUser();
                        storePhoneNumber();
                        changePhoneNumber();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getContext(), "인증에 실패했습니다. 올바른 인증번호를 입력하세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void changePhoneNumber() {
        String oldPhoneNumber = user.getPhone();
        user.setPhone(phoneNumber); // 새 전화번호로 업데이트

        firestoreManager.changePhoneNumber(oldPhoneNumber, phoneNumber, user, new FireStoreManager.firestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                Toast.makeText(getContext(), "전화번호가 변경되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();

                // 자동 로그인 설정 비활성화
                AutomaticLoginChecker.setDisable(requireContext());

                // Room DB의 사용자 데이터 삭제
                UserRepository repository = new UserRepository(requireContext());
                repository.deleteAllUsers();

                // 시작 화면으로 이동
                requireActivity().startActivity(new Intent(requireContext(), StartActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error updating phone number", e);
                Toast.makeText(getContext(), "전화번호 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                user.setPhone(oldPhoneNumber); // 실패시 원래 전화번호로 복구
            }
        });
    }

    private void startNotReceiveCodeTimer() {
        if (showNotReceiveCodeRunnable != null) {
            handler.removeCallbacks(showNotReceiveCodeRunnable);
        }

        showNotReceiveCodeRunnable = () -> notReceiveCodeText.setVisibility(View.VISIBLE);
        handler.postDelayed(showNotReceiveCodeRunnable, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showNotReceiveCodeRunnable != null) {
            handler.removeCallbacks(showNotReceiveCodeRunnable);
        }
    }
}