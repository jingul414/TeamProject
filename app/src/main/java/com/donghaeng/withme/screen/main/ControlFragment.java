package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.donghaeng.withme.R;

import androidx.activity.OnBackPressedCallback;
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
    private User user;
    private UserRepository repository;
    private long backPressedTime = 0;


    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뒤로가기 처리
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finishAffinity();
                    return;
                }
                Toast.makeText(requireContext(), "뒤로가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
                backPressedTime = System.currentTimeMillis();
            }
        });

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
        repository = new UserRepository(requireContext());
    }

    public static ControlFragment newInstance(User user) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadTarget();
        setupBottomNavigation(view);


        // 백그라운드에서 데이터 로드
        new Thread(() -> {
            List<ControlListItem> items = new ArrayList<>();
            UserRepository repository = new UserRepository(requireContext());
            repository.getAllUsers(users -> {
                for (User user : users) {
                    items.add(new ControlListItem(user.getId(), user.getName(), "profile1"));
                }
            });

            // UI 업데이트는 메인 스레드에서
            requireActivity().runOnUiThread(() -> {
                adapter.updateItems(items);  // ControlExpandableAdapter에 updateItems 메서드 추가 필요
            });
        }).start();


        return view;
    }

    private void loadTarget() {
        if (user == null) {
            return;
        }
        List<ControlListItem> items = new ArrayList<>();
        repository.getAllUsers(targets -> {
            for (User target : targets) {
                if (target == null) {
                    continue;
                }
                items.add(new ControlListItem(target.getId(), target.getName(), "profile1"));
            }
            // UI 업데이트는 메인 스레드에서
            requireActivity().runOnUiThread(() -> {
                adapter.updateItems(items);  // ControlExpandableAdapter에 updateItems 메서드 추가 필요
            });
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ControlExpandableAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DataRepository.getInstance().clearCache();
    }

    private void setupBottomNavigation(View view) {
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
    }
}