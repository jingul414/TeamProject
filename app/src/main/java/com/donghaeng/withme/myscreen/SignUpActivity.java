package com.donghaeng.withme.myscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.donghaeng.withme.R;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

// 회원가입 Activity
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    EditText phone_editText;
    TextView phone_verification_text;
    EditText pw_editText;
    EditText pw_check_editText;
    Button sign_up_btn;

    // database 연결
    private FirebaseFirestore db;
    // 인증
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // 모든 firebase 기능 초기화
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                /* 이 콜백이 호출되는 경우 */
                /* 1. 경우에 따라 인증 코드를 보내거나 입력할 필요 없이 전화번호를 즉시 인증하는 경우 */
                /* 2. Google Play 서비스가 수신되는 인증 SMS를 자동으로 감지하고 사용자의 조치 없이도 인증을 수행하는 경우 */
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                /* 전화번호 형식이 유효하지 않은 경우 등 유효하지 않은 인증 요청일 경우 호출 */
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // 유효하지 않은 요청
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // 프로젝트의 SMS 할달량 초과
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // null Activity에서 reCAPTCHA 검증 시도
                    Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                }
                // 메시지 표시 및 UI 업데이트
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                /*
                제공된 전화번호로 SMS 인증 코드가 전송되었으므로 이제 사용자에게 코드를
                입력하도록 요청한 다음 코드를 인증 ID와 결합하여 자격 증명을 구성
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            phone_editText = (EditText) findViewById(R.id.phone_editText);
            phone_verification_text = (TextView) findViewById(R.id.phone_verification_text);
            phone_verification_text.setOnClickListener(new PhoneVerificationTextListener());
            pw_editText = (EditText) findViewById(R.id.pw_editText);
            pw_check_editText = (EditText) findViewById(R.id.pw_check_editText);
            sign_up_btn = (Button) findViewById(R.id.sign_up_btn);
            sign_up_btn.setOnClickListener(new SingUpBtnListener());

            return insets;
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    class PhoneVerificationTextListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String phone = String.valueOf(phone_editText.getText());

            if (phone.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!phone.matches("^\\d{3}-?\\d{3,4}-?\\d{4}$")) {
                Toast.makeText(SignUpActivity.this, "전화번호의 형식이 아닙니다.", Toast.LENGTH_LONG).show();
            }
            phone = phone.replace("-", "");
            phone = "+82" + phone.substring(1);
            Log.d(TAG, "onClick: " + phone);

            // 전화번호 인증
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phone)       // 인증할 핸드폰 번호
                            .setTimeout(60L, TimeUnit.SECONDS) // 시간 초과 기준 및 단위
                            .setActivity(SignUpActivity.this)                 // (optional) Activity for callback binding
                            // If no activity is passed, reCAPTCHA verification can not be used.
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            Log.d(TAG, "onClick: " + phone);
        }
    }

    class SingUpBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String pw = String.valueOf(pw_editText.getText());
            String pw_check = String.valueOf(pw_check_editText.getText());

            if (pw.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (pw_check.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            } else if (!pw.equals(pw_check)) {
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Intent sign_intent = new Intent(SignUpActivity.this, LoginActivity.class);
                Toast.makeText(SignUpActivity.this, "회원가입 성공.", Toast.LENGTH_SHORT).show();
                startActivity(sign_intent);
            }
        }
    }
}