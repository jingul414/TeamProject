package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;


public class TargetConnectFragment extends Fragment {


    public TargetConnectFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // QR 스캔 프래그먼트 추가
        if (savedInstanceState == null) {  // 처음 생성될 때만 추가
            TargetQrFragment qrFragment = new TargetQrFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.child_fragment, qrFragment)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_target_connect, container, false);
    }
}