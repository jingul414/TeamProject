package com.donghaeng.withme.screen.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.ControlAllowanceListChecker;
import com.google.android.material.materialswitch.MaterialSwitch;

/* 제어 허용 목록 (제어자) */
public class FragmentControllerOpt extends Fragment {
    private final String[] KEY_LIST = {ControlAllowanceListChecker.KEY_STORING_NOTICE, ControlAllowanceListChecker.KEY_VOLUME_MODE, ControlAllowanceListChecker.KEY_VOLUME_CONTROL, ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL, ControlAllowanceListChecker.KEY_SETTING_ALARM};
    private MaterialSwitch[] toggles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller_opt, container, false);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        toggles = new MaterialSwitch[]{view.findViewById(R.id.body_toggle1), view.findViewById(R.id.body_toggle2), view.findViewById(R.id.body_toggle3), view.findViewById(R.id.body_toggle4), view.findViewById(R.id.body_toggle5)};

        // 각 토글에 대해 LiveData를 관찰하여 변경사항을 반영
        for (int i = 0; i < toggles.length; i++) {
            final String key = KEY_LIST[i]; // 현재 반복 중인 키를 final로 설정
            sharedViewModel.getToggle(key).observe(getViewLifecycleOwner(), toggles[i]::setChecked);

            // SharedViewModelManager의 LiveData 관찰 추가
            SharedViewModelManager.getInstance().getLiveDataMap().observe(getViewLifecycleOwner(), updatedMap -> {
                if (updatedMap != null && updatedMap.containsKey(key)) {
                    boolean value = updatedMap.get(key);
                    Log.d("Controller", "Key: " + key + ", Value: " + value);
                    updateToggleUI(key, value);
                }
            });
        }

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    // UI 토글 업데이트 메서드
    private void updateToggleUI(String key, boolean value) {
        switch (key) {
            case ControlAllowanceListChecker.KEY_STORING_NOTICE:
                toggles[0].setChecked(value);
                break;
            case ControlAllowanceListChecker.KEY_VOLUME_MODE:
                toggles[1].setChecked(value);
                break;
            case ControlAllowanceListChecker.KEY_VOLUME_CONTROL:
                toggles[2].setChecked(value);
                break;
            case ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL:
                toggles[3].setChecked(value);
                break;
            case ControlAllowanceListChecker.KEY_SETTING_ALARM:
                toggles[4].setChecked(value);
                break;
        }
    }
}