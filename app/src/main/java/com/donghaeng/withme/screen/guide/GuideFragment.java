package com.donghaeng.withme.screen.guide;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.donghaeng.withme.screen.main.TargetMainFragment;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;
    private GuideActivity activity;
    private View back;
    private Button controlInputButton, controlDeleteButton;
    private User user;

    public GuideFragment() {
        // Required empty public constructor
    }

    public static GuideFragment newInstance(User user) {
        GuideFragment fragment = new GuideFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    public static GuideFragment newInstance() {
        return new GuideFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            user = getArguments().getParcelable("user");
        }
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
            activity.onBackPressed();
        });

        controlInputButton = view.findViewById(R.id.control_input_button);
        controlDeleteButton = view.findViewById(R.id.control_delete_button);
        if(user.getUserType() == UserType.TARGET){
            controlInputButton.setVisibility(View.INVISIBLE);
            controlInputButton.setClickable(false);
            controlDeleteButton.setVisibility(View.INVISIBLE);
            controlDeleteButton.setClickable(false);
        }
        controlInputButton.setOnClickListener(v -> {
            activity.changeFragment(GuideInputFragment.newInstance(user));
        });
        controlDeleteButton.setOnClickListener(v -> {
            GuideDeleteDialog dialog = new GuideDeleteDialog(requireContext(), user.getId());
            dialog.show();
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

    public void changeFragment(Fragment fragment){
        activity.changeFragment(fragment);
    }


    public GuideActivity getGuideActivity() {
        return activity;
    }
}