package com.donghaeng.withme.screen.start.connect;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.user.User;
import com.google.android.material.card.MaterialCardView;

public class TargetConnectFragment extends Fragment {
    /**
     * Fragment 생성자 데이터
     */
    private static final String ARG_USER = "user";
    private User user;

    private User opponent;
    private TextView titleTextView;

    public TargetConnectFragment() {
        // Required empty public constructor
    }

    public static TargetConnectFragment newInstance(User user) {
        TargetConnectFragment fragment = new TargetConnectFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.textView4);

        // QR 스캔 프래그먼트 추가
        if (savedInstanceState == null) {  // 처음 생성될 때만 추가
            TargetQrFragment qrFragment = TargetQrFragment.newInstance(user);
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

    public void changeFragment(String fragmentName) {
        switch (fragmentName) {
            case "info":
                titleTextView.setText("보호자의 정보를 확인하세요");
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.child_fragment, ConnectInfoFragment.newInstance(user, opponent))
                        .commit();
                break;
            case "qr":
                titleTextView.setText("보호자에게 QR을 보여주세요");
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.child_fragment, TargetQrFragment.newInstance(user))
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