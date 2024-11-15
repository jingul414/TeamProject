package com.donghaeng.withme.screen.guide;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;
    private GuideActivity activity;
    private View back;

    public GuideFragment() {
        // Required empty public constructor
    }

    public static GuideFragment newInstance() {
        return new GuideFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        activity = (GuideActivity) requireActivity();
        back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });


        // 처음 리스트 헤더들 설정
        List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("guide", ListItem.TYPE_HEADER, "동행 설명서"));
        items.add(new ListItem("smartphone", ListItem.TYPE_HEADER, "스마트폰 설명서"));
        items.add(new ListItem("guardian", ListItem.TYPE_HEADER, "보호자의 설명"));

        adapter = new ExpandableAdapter(items, this);
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 앱이 종료될 때 캐시 정리
        DataRepository.getInstance().clearCache();
    }

    public void changeFragment(Fragment fragment) {
        activity.changeFragment(fragment);
    }
}