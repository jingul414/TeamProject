package com.donghaeng.withme.screen.setting;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.donghaeng.withme.R;

import androidx.appcompat.app.AlertDialog;

public class FragmentSetting extends Fragment {
    private SettingActivity activity;

    private int FragmentMode = -1;  // 임시 변수들
    private final int PERMIT = 0;
    private final int LEAVE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    TODO 제어자인지 피제어자인지 받아와야 함
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        View back;
        Button change_number, change_pw, permit_list, leave;
        Button control, target; // 임시 버튼들 -> 제어 허용 목록 버튼 클릭 시 제어자 피제어자 선택

        activity = (SettingActivity) requireActivity();
        back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        change_number = view.findViewById(R.id.change_number);
        change_pw = view.findViewById(R.id.change_pw);
        permit_list = view.findViewById(R.id.permit_list);
        leave = view.findViewById(R.id.leave);

        change_number.setOnClickListener(v -> {
            activity.changeFragment("ChangeNumber");
        });
        change_pw.setOnClickListener(v -> {
            activity.changeFragment("ChangePW");
        });
        permit_list.setOnClickListener(v -> {
            // activity.changeFragment("PermitList"); 임시 버튼 사용
            view.findViewById(R.id.buttons).setVisibility(View.VISIBLE);
            FragmentMode = PERMIT;
        });
        leave.setOnClickListener(v -> {
//             activity.changeFragment("Leave"); 임시 버튼 사용
            view.findViewById(R.id.buttons).setVisibility(View.VISIBLE);
            FragmentMode = LEAVE;
        });

        // 임시 버튼들 초기화
        control = view.findViewById(R.id.control);
        target = view.findViewById(R.id.target);
        control.setOnClickListener(v -> {
            if(FragmentMode == PERMIT){
                activity.changeFragment("PermitListControl");
            } else if(FragmentMode == LEAVE){
                showLeaveDialog(true); // true 는 controller 용 다이얼로그
            }
        });
        target.setOnClickListener(v -> {
            if(FragmentMode == PERMIT){
                activity.changeFragment("PermitListTarget");
            } else if(FragmentMode == LEAVE){
                showLeaveDialog(false); // false 는 target 용 다이얼로그
            }
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
            // TODO: 회원 탈퇴 처리 로직 구현 -> 현재는 로그인 화면으로 이동 됨
            dialog.dismiss();
            requireActivity().finish(); // 또는 로그인 화면으로 이동
        });

        // 아니오 버튼 클릭 리스너
        Button noBtn = dialogView.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // buttons 레이아웃을 다시 숨김
            requireView().findViewById(R.id.buttons).setVisibility(View.INVISIBLE);
            FragmentMode = -1;
        });

        // 다이얼로그가 취소되면 버튼들을 숨김
        dialog.setOnCancelListener(dialogInterface -> {
            requireView().findViewById(R.id.buttons).setVisibility(View.INVISIBLE);
            FragmentMode = -1;
        });

        // 다이얼로그 표시
        dialog.show();
    }
}