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
import com.donghaeng.withme.data.message.firebasemessage.SendDataMessage;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.screen.guide.GuideActivity;
import com.donghaeng.withme.screen.setting.SettingActivity;
import com.donghaeng.withme.service.RejectionManager;
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
        TextView controlStatusText = view.findViewById(R.id.control_status_text);

        SendDataMessage sendDataMessage = new SendDataMessage();

        View allowButton = view.findViewById(R.id.accept_btn);
        View rejectButton = view.findViewById(R.id.reject_btn);

        // 초기 상태 확인 및 설정
        repository.getAllUsers(users -> {
            for (User controller : users) {
                if (controller != null) {
                    boolean isRejected = RejectionManager.getInstance(requireContext())
                            .isRejected(controller.getToken());
                    updateControlStatus(controlStatusText, isRejected, controller.getToken());
                    break;
                }
            }
        });

        rejectButton.setOnClickListener(v -> {
            repository.getAllUsers(users -> {
                for (User controller : users) {
                    if (controller != null) {
                        TimeRejectDialog dialog = new TimeRejectDialog(requireContext(), controller.getToken());
                        dialog.setOnDismissListener(dialogInterface -> {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    updateControlStatus(controlStatusText, true, controller.getToken());
                                });
                            }
                        });
                        dialog.show();
                        break;
                    }
                }
            });
        });

        allowButton.setOnClickListener(v -> {
            repository.getAllUsers(users -> {
                for (User controller : users) {
                    if (controller != null) {
                        sendDataMessage.sendDataMessage(controller.getToken(), "reject", "accept");
                        RejectionManager.getInstance(requireContext())
                                .removeRejection(controller.getToken());
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                updateControlStatus(controlStatusText, false, controller.getToken());
                            });
                        }
                        break;
                    }
                }
            });
        });
    }

    private void updateControlStatus(TextView statusText, boolean isRejected, String token) {
        if (isRejected) {
            int remainingMinutes = RejectionManager.getInstance(requireContext())
                    .getRemainingMinutes(token);
            statusText.setText(String.format("제어 거절 상태 (%d분 남음)", remainingMinutes));
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            statusText.setText("제어 허용 상태");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
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