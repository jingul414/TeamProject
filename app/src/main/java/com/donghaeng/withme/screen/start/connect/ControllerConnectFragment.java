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
import com.donghaeng.withme.data.user.User;

public class ControllerConnectFragment extends Fragment {
    /**
     * Fragment 생성자 데이터
     */
    private static final String ARG_USER = "user";
    private User user;

    private User opponent;

    public ControllerConnectFragment() {
        // Required empty public constructor
    }

    public static ControllerConnectFragment newInstance(User user) {
        ControllerConnectFragment fragment = new ControllerConnectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_controller_connect, container, false);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // QR 스캔 프래그먼트 추가
        if (savedInstanceState == null) {  // 처음 생성될 때만 추가
            ControllerQrFragment qrFragment = ControllerQrFragment.newInstance(user);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.child_fragment, qrFragment)
                    .commit();
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void changeFragment(String fragmentName) {
        switch (fragmentName) {
            case "info":
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.child_fragment, ConnectInfoFragment.newInstance(user, opponent))
                        .commit();
                break;
            case "qr":
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.child_fragment, ControllerQrFragment.newInstance(user))
                        .commit();
                break;
            default:
                break;
        }
    }
    public void setOpponentUserInfo(User opponent) {
        this.opponent = opponent;
    }
}