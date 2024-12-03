package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.screen.guide.GuideActivity;
import com.donghaeng.withme.screen.setting.SettingActivity;
import com.donghaeng.withme.screen.start.connect.TargetQrFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;


public class TargetMainFragment extends Fragment {
    private RecyclerView recyclerView;
    private TargetExpandableAdapter adapter;

    private static final String ARG_USER = "user";
    private User user;

    public TargetMainFragment() {
        // Required empty public constructor
    }


    public static TargetMainFragment newInstance(User user) {
        TargetMainFragment fragment = new TargetMainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_main, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 컨트롤 리스트 초기화
        // Todo 현재는 테스트용 추가, 서버에서 받아오는 기능 추가 필요
        List<TargetListItem> items = new ArrayList<>();
        items.add(new TargetListItem("log1", "통화 소리 증가", "홍길동", "12월 31일 23시 59분 58초"));
        items.add(new TargetListItem("log2", "화면 밝기 증가", "홍길동", "12월 31일 23시 59분 59초"));
        items.add(new TargetListItem("log1", "통화 소리 증가", "홍길동", "12월 31일 23시 59분 58초"));
        items.add(new TargetListItem("log2", "화면 밝기 증가", "홍길동", "12월 31일 23시 59분 59초"));
        items.add(new TargetListItem("log1", "통화 소리 증가", "홍길동", "12월 31일 23시 59분 58초"));
        items.add(new TargetListItem("log2", "화면 밝기 증가", "홍길동", "12월 31일 23시 59분 59초"));

        adapter = new TargetExpandableAdapter(items);
        recyclerView.setAdapter(adapter);

        // 네비게이션 바 설정
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_guide) {
                intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra("user", (Parcelable) user);
                requireActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (itemId == R.id.nav_home) {
                // home 관련 처리
            } else if (itemId == R.id.nav_setting) {
                intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("user", (Parcelable) user);
                requireActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });

            return view;

    }
}