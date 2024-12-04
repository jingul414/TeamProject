package com.donghaeng.withme.screen.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.ControlAllowanceListChecker;
import com.google.android.material.materialswitch.MaterialSwitch;

/* 제어 허용 목록 (피제어자) */
public class FragmentTargetOpt extends Fragment {
    private SharedViewModel sharedViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_opt, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        MaterialSwitch[] toggles = new MaterialSwitch[]{
                view.findViewById(R.id.body_toggle1),
                view.findViewById(R.id.body_toggle2),
                view.findViewById(R.id.body_toggle3),
                view.findViewById(R.id.body_toggle4),
                view.findViewById(R.id.body_toggle5)
        };

        String[] keys = {
                ControlAllowanceListChecker.KEY_STORING_NOTICE,
                ControlAllowanceListChecker.KEY_VOLUME_MODE,
                ControlAllowanceListChecker.KEY_VOLUME_CONTROL,
                ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL,
                ControlAllowanceListChecker.KEY_SETTING_ALARM
        };

        for (int i = 0; i < toggles.length; i++) {
            String key = keys[i];
            MaterialSwitch toggle = toggles[i];

            // 초기 상태 설정
            sharedViewModel.getToggle(key).observe(getViewLifecycleOwner(), toggle::setChecked);

            // 처음 View가 생성될 때 초기 상태 설정 명시적으로 수행
            Boolean initialValue = sharedViewModel.getToggle(key).getValue();
            if (initialValue != null) {
                toggle.setChecked(initialValue);
            }

            // 토글 상태 변경 시 SharedViewModel에 반영
            toggle.setOnCheckedChangeListener((buttonView, isChecked) -> sharedViewModel.setToggle(key, isChecked));
        }

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}