package com.donghaeng.withme.screen.guide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.donghaeng.withme.R;


public class GuideInfoFragment extends Fragment {
    private String data; // 현재는 가이드에서 누를 아이템의 텍스트를 받아와서 여기에 저장..
    private TextView title;
    private View back;

    public GuideInfoFragment(String str) {
        data = str;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_info, container, false);

        back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        title = view.findViewById(R.id.head_text);
        title.setText(data);

        return view;
    }
}