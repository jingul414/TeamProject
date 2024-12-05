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

import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.screen.start.StartActivity;

import org.mindrot.jbcrypt.BCrypt;

public class SettingPasswordFragment extends Fragment {
    private static final String TAG = "SettingPasswordFragment";
    private User user;
    private EditText currentPasswordEdit;
    private EditText newPasswordEdit;
    private EditText newPasswordCheckEdit;
    private TextView warningText;
    private Button changeButton;
    private FireStoreManager firestoreManager;
    private Handler handler;
    private Runnable checkPasswordRunnable;

    public SettingPasswordFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_password, container, false);

        initializeViews(view);
        setupPasswordWarning();
        setupChangeButton();

        return view;
    }

    private void initializeViews(View view) {
        currentPasswordEdit = view.findViewById(R.id.current_password);
        newPasswordEdit = view.findViewById(R.id.signup_edit_password);
        newPasswordCheckEdit = view.findViewById(R.id.signup_edit_password_check);
        warningText = view.findViewById(R.id.signup_text_warning);
        changeButton = view.findViewById(R.id.signup_btn_next);
        firestoreManager = FireStoreManager.getInstance();

        warningText.setVisibility(View.INVISIBLE);
    }

    private void setupPasswordWarning() {
        handler = new Handler();
        checkPasswordRunnable = () -> {
            if (warningText.getVisibility() == View.VISIBLE)
                warningText.setVisibility(View.INVISIBLE);
        };
    }

    private void setupChangeButton() {
        changeButton.setOnClickListener(v -> validateAndChangePassword());
    }

    private void validateAndChangePassword() {
        String currentPassword = currentPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String newPasswordCheck = newPasswordCheckEdit.getText().toString();

        // 현재 비밀번호 검증
        if (!BCrypt.checkpw(currentPassword, user.getHashedPassword())) {
            Toast.makeText(getContext(), "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 비밀번호 유효성 검사
        if (newPassword.isEmpty()) {
            Toast.makeText(getContext(), "새 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 8) {
            Toast.makeText(getContext(), "비밀번호를 8자 이상 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (newPasswordCheck.isEmpty()) {
            Toast.makeText(getContext(), "새 비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPasswordCheck.length() < 8) {
            Toast.makeText(getContext(), "비밀번호를 8자 이상 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!newPassword.equals(newPasswordCheck)) {
            if (warningText.getVisibility() == View.INVISIBLE) {
                warningText.setVisibility(View.VISIBLE);
                handler.removeCallbacks(checkPasswordRunnable);
                handler.postDelayed(checkPasswordRunnable, 2000);
            }
            return;
        }

        // FireStoreManager의 changePW 메서드를 사용하여 비밀번호 변경
        firestoreManager.changePW(user.getPhone(), newPassword, new FireStoreManager.firestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                // 로컬 User 객체의 비밀번호도 업데이트
                String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                user.setHashedPassword(hashedNewPassword);

                Toast.makeText(getContext(), "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                
                // 비밀번호 변경 성공 시 다시 로그인 하도록 StartActivity 로 이동
                // 자동 로그인 설정 비활성화
                AutomaticLoginChecker.setDisable(requireContext());

                // Room DB의 사용자 데이터 삭제
                UserRepository repository = new UserRepository(requireContext());
                repository.deleteAllUsers();

                // 시작 화면으로 이동 (현재 액티비티 종료)
                requireActivity().startActivity(new Intent(requireContext(), StartActivity.class));
                requireActivity().finish();            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error updating password", e);
                Toast.makeText(getContext(), "비밀번호 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
