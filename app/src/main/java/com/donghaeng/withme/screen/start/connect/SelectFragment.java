package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.donghaeng.withme.R;
import com.donghaeng.withme.screen.start.StartActivity;

public class SelectFragment extends Fragment {
    LinearLayout controller_btn;
    LinearLayout target_btn;
    StartActivity startActivity;

    public SelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        controller_btn = view.findViewById(R.id.controller_btn);
        target_btn = view.findViewById(R.id.target_btn);

        startActivity = (StartActivity) getActivity();

        controller_btn.setOnClickListener(v -> startActivity.changeFragment("controller_QR"));
        target_btn.setOnClickListener(v -> startActivity.changeFragment("target_QR"));
        return view;
    }
}