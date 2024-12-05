package com.donghaeng.withme.screen.setting;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.processor.PhoneFormatUtil;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.donghaeng.withme.screen.main.ControlListItem;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private SettingActivity activity;
    private TextView userName;
    private TextView userPhoneNumber;
    private User user;

    public static Fragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        Fragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        View back;
        Button change_number, change_pw, permit_list, leave, logout;

        activity = (SettingActivity) requireActivity();
        back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        userName = view.findViewById(R.id.user_name);
        userPhoneNumber = view.findViewById(R.id.user_phone_number);
        userName.setText(user.getName());
        userPhoneNumber.setText(PhoneFormatUtil.phone(user.getPhone()));

        change_number = view.findViewById(R.id.change_number);
        change_pw = view.findViewById(R.id.change_pw);
        permit_list = view.findViewById(R.id.permit_list);
        leave = view.findViewById(R.id.leave);
        logout = view.findViewById(R.id.logout);

        change_number.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new SettingPhoneNumFragment(user));
            transaction.addToBackStack(null); // 뒤로 가기 버튼으로 돌아가기 가능
            transaction.commit();
        });

        change_pw.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new SettingPasswordFragment(user));
            transaction.addToBackStack(null); // 뒤로 가기 버튼으로 돌아가기 가능
            transaction.commit();
        });
        byte usetType = user.getUserType();

        permit_list.setOnClickListener(v -> {
            if(usetType == UserType.CONTROLLER){
                activity.changeFragment("PermitListControl");
            } else if (usetType == UserType.TARGET) {
                activity.changeFragment("PermitListTarget");
            }
        });
        leave.setOnClickListener(v -> {
            if(usetType == UserType.CONTROLLER){
                showLeaveDialog(true); // true 는 controller 용 다이얼로그
            } else if (usetType == UserType.TARGET) {
                showLeaveDialog(false); // false 는 target 용 다이얼로그
            }
        });
        logout.setOnClickListener(v -> {
            // 자동 로그인 설정 비활성화
            AutomaticLoginChecker.setDisable(requireContext());

            // Room DB의 사용자 데이터 삭제
            UserRepository repository = new UserRepository(requireContext());
            repository.deleteAllUsers();

            // 시작 화면으로 이동 (현재 액티비티 종료)
            requireActivity().startActivity(new Intent(requireContext(), StartActivity.class));
            requireActivity().finish();

            // 토스트 메시지 표시 (선택사항)
            Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }


    // 다이얼로그 설정
    private void showLeaveDialog(boolean isController) {
        // 다이얼로그 빌더 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // 커스텀 레이아웃 인플레이트
        View dialogView = getLayoutInflater().inflate(
                isController ? R.layout.dialog_controller_leave : R.layout.dialog_target_leave,
                null
        );
        builder.setView(dialogView);

        // 다이얼로그 생성
        final AlertDialog dialog = builder.create();

        // 배경을 투명하게 설정
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 예 버튼 클릭 리스너
        Button yesBtn = dialogView.findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(v -> {
            // Firebase에서 사용자 데이터 삭제
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 사용자 데이터 삭제
            String hashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(user.getPhone());
            db.collection("user").document(hashedPhoneNum)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Firebase 삭제 성공 후 로컬 데이터 정리

                        // 자동 로그인 설정 비활성화
                        AutomaticLoginChecker.setDisable(requireContext());

                        // Room DB의 사용자 데이터 삭제
                        UserRepository repository = new UserRepository(requireContext());
                        repository.deleteAllUsers();

                        // 토스트 메시지 표시
                        Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                        // 시작 화면으로 이동
                        Intent intent = new Intent(requireContext(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // 실패 시 에러 메시지
                        Toast.makeText(requireContext(), "회원 탈퇴 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
        });


        // 아니오 버튼 클릭 리스너
        Button noBtn = dialogView.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // buttons 레이아웃을 다시 숨김
        });

        // 다이얼로그가 취소되면 버튼들을 숨김
        dialog.setOnCancelListener(dialogInterface -> {
        });

        // 다이얼로그 표시
        dialog.show();
    }
}