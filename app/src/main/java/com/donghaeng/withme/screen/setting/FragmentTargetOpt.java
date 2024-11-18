package com.donghaeng.withme.screen.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;


public class FragmentTargetOpt extends Fragment {
    private SettingActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_target_opt, container, false);

        View back;

        activity = (SettingActivity) requireActivity();
        back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        return view;
    }
}