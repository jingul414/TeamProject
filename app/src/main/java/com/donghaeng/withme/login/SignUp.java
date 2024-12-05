package com.donghaeng.withme.login;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.donghaeng.withme.exception.phonenumber.EmptyPhoneNumberException;
import com.donghaeng.withme.exception.phonenumber.InvalidPhoneNumberException;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.data.user.Undefined;
import com.donghaeng.withme.data.user.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.TimeUnit;

// 회원가입
public class SignUp {

    private static final String TAG = "SignUp";
    StartActivity startActivity;

    private String name;
    private String phoneNumber;
    private String hashedPassword;
    private String userID;

    // 전화번호 인증 시 사용
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FireStoreManager firestoreManager;

    // 비밀번호 경고문
    private Handler handler;
    private Runnable checkPasswordRunnable;

    // 유저 정보 최종 저장
    private FirebaseUser mFirebaseUser;
    private User user;

    public SignUp(StartActivity startActivity) {
        // 회원가입 생성
        this.startActivity = startActivity;
    }

    // 이름 저장
    public void storeName(EditText nameEdit) {
        String name = nameEdit.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(startActivity, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        } else {
            this.name = name;
            startActivity.changeFragment("SignupVerifyingPhoneNumberFragment");
        }
    }

    // 전화번호 인증 과정
    public void initializePhoneAuth() {
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(startActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        mFirebaseUser = task.getResult().getUser();
                        storePhoneNumber();
                        // Update UI
                        startActivity.changeFragment("SignupPassWordFragment");
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(startActivity, "인증에 실패했습니다. 올바른 인증번호를 입력하세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void checkVerificationCode(EditText verificationCodeEdit) {
        String verificationCode = String.valueOf(verificationCodeEdit.getText());
        if (verificationCode.isEmpty()) {
            Toast.makeText(startActivity, "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (verificationCode.length() != 6) {
            Toast.makeText(startActivity, "올바르지 않은 인증번호 양식입니다.", Toast.LENGTH_LONG).show();
        } else {
            verifyPhoneNumberWithCode(mVerificationId, verificationCode);
        }
    }

    public void sendVerificationCode(EditText phoneNumberEdit) throws EmptyPhoneNumberException, InvalidPhoneNumberException {
        phoneNumber = String.valueOf(phoneNumberEdit.getText());

        if (phoneNumber.isEmpty()) {
            throw new EmptyPhoneNumberException("전화번호를 입력하세요.");
        } else if (!phoneNumber.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) {
            throw new InvalidPhoneNumberException("전화번호의 형식이 아닙니다.");
        } else {
            changeToAvailablePhoneNumber();

            // 전화번호 인증
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)       // 인증할 핸드폰 번호
                            .setTimeout(60L, TimeUnit.SECONDS) // 시간 초과 기준 및 단위
                            .setActivity(startActivity)                 // (optional) Activity for callback binding
                            // If no activity is passed, reCAPTCHA verification can not be used.
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            Log.w(TAG, "전화번호: " + phoneNumber);
        }
    }

    public void resendVerificationCode() {
        changeToAvailablePhoneNumber();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(startActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mResendToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // TODO: 안 뜨는 경우는 해결, but 중복 출력 가능성 => 딜레이 발생 (Toast 방식 폐기 요망)
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Log.e(TAG, "Verification failed: " + e.getMessage());
            Toast.makeText(startActivity, "인증에 실패했습니다. 올바른 인증번호를 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    private void changeToAvailablePhoneNumber() {
        if (!phoneNumber.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) return;
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = "+82" + phoneNumber.substring(1);
    }

    public void storePhoneNumber() {
        phoneNumber = "0" + phoneNumber.substring(3);
    }

    // 비밀번호 저장 과정
    public void makeAlertTask(TextView warningText) {
        handler = new Handler();
        checkPasswordRunnable = () -> {
            if (warningText.getVisibility() == View.VISIBLE)
                warningText.setVisibility(View.INVISIBLE);
        };
    }

    public void checkPassWord(EditText pwEdit, EditText pwCheckEdit, TextView warningText) {
        String pw = pwEdit.getText().toString();
        String pwCheck = pwCheckEdit.getText().toString();

        if (pw.isEmpty()) {
            Toast.makeText(startActivity, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (pw.length() < 8) {
            Toast.makeText(startActivity, "비밀번호를 8자 이상 입력하세요.", Toast.LENGTH_LONG).show();
        } else if (pwCheck.isEmpty()) {
            Toast.makeText(startActivity, "비밀번호 확인(재입력)을 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (!pw.equals(pwCheck)) {
            if (warningText.getVisibility() == View.INVISIBLE) {
                warningText.setVisibility(View.VISIBLE);
                handler.removeCallbacks(checkPasswordRunnable); // 기존 작업 제거
                handler.postDelayed(checkPasswordRunnable, 2000); // 2초 후 실행
            }
        } else {
            storeHashedPassword(pw);
            setUser();
            setUserData();
            if(startActivity.getUser() instanceof Undefined) startActivity.changeFragment("SelectFragment");
        }
    }

    public void setUserData(){
        firestoreManager = FireStoreManager.getInstance();
        firestoreManager.setUserData(user,new FireStoreManager.firestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("Firestore", result.toString());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Error saving user data", e);
            }
        } );
    }

    public void storeHashedPassword(String password) {
        hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // 비밀번호를 해시하여 저장
        Log.w(TAG, "비밀번호: " + hashedPassword);
    }

    public void setUser(){
        userID = mFirebaseUser.getUid();
        user = new Undefined(name, phoneNumber, userID, hashedPassword);
        startActivity.setUser(user);
    }
}