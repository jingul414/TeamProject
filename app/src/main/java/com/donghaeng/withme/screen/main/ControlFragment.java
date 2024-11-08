package com.donghaeng.withme.screen.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donghaeng.withme.R;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.screen.guide.DataRepository;

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


        // 컨트롤 리스트 초기화
        List<ControlListItem> items = new ArrayList<>();
        items.add(new ControlListItem("user1", "홍길동", "profile1"));
        items.add(new ControlListItem("user2", "홍길순", "profile2"));

        adapter = new ControlExpandableAdapter(items);
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DataRepository.getInstance().clearCache();
    }

}