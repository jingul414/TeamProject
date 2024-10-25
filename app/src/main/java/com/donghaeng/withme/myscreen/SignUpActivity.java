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

// 회원가입 액티비티
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    EditText phone_editText;
    TextView phone_verification_text;
    EditText pw_editText;
    EditText pw_check_editText;
    Button sign_up_btn;

    // database 연결
    private FirebaseFirestore db;
    // 인증 연결
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
                // 이 콜백은 두 가지 상황에서 호출됩니다:
                // 1 - 즉시 인증. 경우에 따라 전화번호는 인증 코드를 보내거나 입력하지 않고도 즉시 인증될 수 있습니다.
                // 2 - 자동 검색. 일부 기기에서 Google Play 서비스는 수신되는 인증 SMS를 자동으로 감지하여 사용자 작업 없이 인증을 수행할 수 있습니다.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }
                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
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
            // 전화번호 인증
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(SignUpActivity.this)                 // (optional) Activity for callback binding
                            // If no activity is passed, reCAPTCHA verification can not be used.
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
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