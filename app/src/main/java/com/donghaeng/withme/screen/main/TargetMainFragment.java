package com.donghaeng.withme.screen.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneNumberUtils;
import android.util.Log;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.screen.guide.GuideActivity;
import com.donghaeng.withme.screen.setting.SettingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TargetMainFragment extends Fragment {
    private static final String TAG = "TargetMainFragment";
    private TextView controllerNameTextView;
    private TextView controllerPhoneNumberTextView;
    private RecyclerView recyclerView;
    private TargetExpandableAdapter adapter;
    private FirebaseFirestore db;
    private UserRepository repository;

    private static final String ARG_USER = "user";
    private User user;
    private long backPressedTime = 0;

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
            user = getArguments().getParcelable(ARG_USER);
        }
        db = FirebaseFirestore.getInstance();
        repository = new UserRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_main, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadControllerData();
        setupBottomNavigation(view);

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        controllerNameTextView = view.findViewById(R.id.name_textview);
        controllerPhoneNumberTextView = view.findViewById(R.id.number_textview);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TargetExpandableAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadControllerData() {
        if (user == null) {
            Log.e(TAG, "User is null!");
            return;
        }

        repository.getAllUsers(controllers -> {
            for (User controller : controllers) {
                if (controller == null) {
                    Log.e(TAG, "Controller is null!");
                    continue;
                }
                updateControllerInfo(controller);
                setupRealtimeUpdates(user.getId(), controller.getId());
            }
        });
    }

    private void updateControllerInfo(User controller) {
        controllerNameTextView.setText(controller.getName());
        controllerPhoneNumberTextView.setText(PhoneNumberUtils.formatNumber(controller.getPhone(), Locale.getDefault().getCountry()));
    }

    private void writeLogData(User controller) {
        // TODO: 현재로서는 피제어자가 쓸 이유가 없음. 롤백 기능 구현하거나, 제어자에서 코드 사용하면 됨.
        String time = getCurrentTime();

        Map<String, Object> logData = new HashMap<>();
        logData.put("control", "테스트");
        logData.put("name", controller.getName());
        logData.put("time", time);

        DocumentReference docRef = db.collection("log")
                .document(user.getId())
                .collection(controller.getId())
                .document(String.valueOf(System.currentTimeMillis()));

        docRef.set(logData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "로그 기록 성공!"))
                .addOnFailureListener(e -> Log.e(TAG, "로그 기록 실패", e));
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();  // 현재 시간 불러오기
        Date currentDate = calendar.getTime();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREAN);
        return outputFormat.format(currentDate);
    }

    private void setupRealtimeUpdates(String userId, String controllerId) {
        db.collection("log")
                .document(userId)
                .collection(controllerId)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshots == null) {
                        Log.e(TAG, "Snapshot is null");
                        return;
                    }

                    List<TargetListItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String control = doc.getString("control");
                        String name = doc.getString("name");
                        String time = doc.getString("time");

                        if (control != null && name != null && time != null) {
                            items.add(new TargetListItem(doc.getId(), control, name, time));
                        } else {
                            Log.w(TAG, "Missing fields in document: " + doc.getId());
                        }
                    }

                    updateRecyclerView(items);
                });
    }

    private void updateRecyclerView(List<TargetListItem> items) {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                adapter = new TargetExpandableAdapter(items);
                recyclerView.setAdapter(adapter);
            });
        }
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