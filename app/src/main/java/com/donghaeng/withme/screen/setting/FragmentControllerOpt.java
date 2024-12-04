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

/* 제어 허용 목록 (제어자) */
public class FragmentControllerOpt extends Fragment {
    private final String[] KEY_LIST = {
            ControlAllowanceListChecker.KEY_STORING_NOTICE,
            ControlAllowanceListChecker.KEY_VOLUME_MODE,
            ControlAllowanceListChecker.KEY_VOLUME_CONTROL,
            ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL,
            ControlAllowanceListChecker.KEY_SETTING_ALARM
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller_opt, container, false);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        MaterialSwitch[] toggles = new MaterialSwitch[]{
                view.findViewById(R.id.body_toggle1),
                view.findViewById(R.id.body_toggle2),
                view.findViewById(R.id.body_toggle3),
                view.findViewById(R.id.body_toggle4),
                view.findViewById(R.id.body_toggle5)
        };

        // 각 토글에 대해 LiveData를 관찰하여 변경사항을 반영
        for (int i = 0; i < toggles.length; i++) {
            sharedViewModel.getToggle(KEY_LIST[i]).observe(getViewLifecycleOwner(), toggles[i]::setChecked);
        }

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}