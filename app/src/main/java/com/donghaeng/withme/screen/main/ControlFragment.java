package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.screen.guide.DataRepository;
import com.donghaeng.withme.screen.guide.GuideActivity;
import com.donghaeng.withme.screen.setting.SettingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ControlFragment extends Fragment {
    private RecyclerView recyclerView;
    private ControlExpandableAdapter adapter;

    public ControlFragment() {
        // Required empty public constructor
    }

    public static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        /* 피제어자 모두 불러오기 */
        UserRepository repository = new UserRepository(requireContext());
        List<User> users = repository.getAllUsers();
        List<ControlListItem> items = new ArrayList<>();
        for(User user : users) {
            // TODO: 프로필 이미지 변경
            items.add(new ControlListItem(user.getId(), user.getName(), "profile1"));
        }

        adapter = new ControlExpandableAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);

        // 네비게이션 바 설정
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_guide) {
                intent = new Intent(getActivity(), GuideActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (itemId == R.id.nav_home) {
                // home 관련 처리
            } else if (itemId == R.id.nav_setting) {
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DataRepository.getInstance().clearCache();
    }

}