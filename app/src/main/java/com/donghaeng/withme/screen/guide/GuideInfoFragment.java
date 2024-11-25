package com.donghaeng.withme.screen.guide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.donghaeng.withme.R;

import org.w3c.dom.Text;


public class GuideInfoFragment extends Fragment {
    private String data; // json 데이터 저장
    private String jsonContent;
    private TextView title;
    private View back;
    private RecyclerView recyclerView;


    public GuideInfoFragment(String title, String json) {
        data = title;
        this.jsonContent = json;
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

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GuideContentAdapter adapter = new GuideContentAdapter(getContext(), jsonContent);
        recyclerView.setAdapter(adapter);

        return view;
    }
}