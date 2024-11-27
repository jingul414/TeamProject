package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;


public class ConnectInfoFragment extends Fragment {
    private ControllerConnectFragment connectFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_info, container, false);

        connectFragment = (ControllerConnectFragment) getParentFragment();

        // 아니오 버튼 클릭시 뒤로 이동
        View back = view.findViewById(R.id.no_button);
        back.setOnClickListener(v -> {
            connectFragment.changeFragment("qr");
        });
        return view;
    }
}