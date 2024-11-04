package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;

public class ControllerConnectFragment extends Fragment {

    public ControllerConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller_connect, container, false);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // QR 스캔 프래그먼트 추가
        if (savedInstanceState == null) {  // 처음 생성될 때만 추가
            ControllerQrFragment qrFragment = new ControllerQrFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.child_fragment, qrFragment)
                    .commit();
        }
    }

    public void changeFragment(String fragmentName) {
        switch (fragmentName) {
            case "info":
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.child_fragment, new ConnectInfoFragment())
                        .commit();
                break;
            default:
                break;
        }
    }
}