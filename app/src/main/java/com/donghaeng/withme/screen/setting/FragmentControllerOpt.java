package com.donghaeng.withme.screen.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import com.google.android.material.materialswitch.MaterialSwitch;


public class FragmentControllerOpt extends Fragment {
    private SettingActivity activity;
    private SharedViewModel sharedViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controller_opt, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        MaterialSwitch bodyToggle1 = view.findViewById(R.id.body_toggle1);
        MaterialSwitch bodyToggle2 = view.findViewById(R.id.body_toggle2);
        MaterialSwitch bodyToggle3 = view.findViewById(R.id.body_toggle3);
        MaterialSwitch bodyToggle4 = view.findViewById(R.id.body_toggle4);
        MaterialSwitch bodyToggle5 = view.findViewById(R.id.body_toggle5);

        sharedViewModel.getToggle1().observe(getViewLifecycleOwner(), bodyToggle1::setChecked);
        sharedViewModel.getToggle2().observe(getViewLifecycleOwner(), bodyToggle2::setChecked);
        sharedViewModel.getToggle3().observe(getViewLifecycleOwner(), bodyToggle3::setChecked);
        sharedViewModel.getToggle4().observe(getViewLifecycleOwner(), bodyToggle4::setChecked);
        sharedViewModel.getToggle5().observe(getViewLifecycleOwner(), bodyToggle5::setChecked);

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}