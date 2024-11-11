package com.donghaeng.withme.screen.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    private boolean isControlPanelVisible = false;

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

        /*
        // 화살표 아이콘과 제어 패널을 연결
        ImageView arrowIcon1 = view.findViewById(R.id.arrowIcon1);
        final View controlPanel = view.findViewById(R.id.controlPanel);

        // arrowIcon1 클릭 시 제어 패널 보이기/숨기기
        arrowIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isControlPanelVisible) {
                    controlPanel.setVisibility(View.GONE);
                    arrowIcon1.setImageResource(R.drawable.ic_arrow_down); // 아이콘을 아래 화살표로 변경
                } else {
                    controlPanel.setVisibility(View.VISIBLE);
                    arrowIcon1.setImageResource(R.drawable.ic_arrow_up); // 아이콘을 위 화살표로 변경
                }
                isControlPanelVisible = !isControlPanelVisible; // 상태 반전
            }
        });*/

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DataRepository.getInstance().clearCache();
    }

}