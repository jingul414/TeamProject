package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.user.Undefined;
import com.donghaeng.withme.user.User;
import com.donghaeng.withme.user.UserType;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectInfoFragment extends Fragment {
    /**
     * Fragment 생성자 데이터
     */
    private static final String ARG_USER = "user";
    private User user;

    public ConnectInfoFragment() {
        // Required empty public constructor
    }

    public static ConnectInfoFragment newInstance(User user) {
        ConnectInfoFragment fragment = new ConnectInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_info, container, false);

        TextView infoTextView = view.findViewById(R.id.info_text);
        TextView nameTextView = view.findViewById(R.id.tel_name);
        TextView phoneTextView = view.findViewById(R.id.tel_text);
        if (user != null) {
            switch (user.getUserType()) {
                case UserType.CONTROLLER:
                    infoTextView.setText("보호자 정보");
                    break;
                case UserType.TARGET:
                    infoTextView.setText("동행인 정보");
                    break;
                default:
                    infoTextView.setText("알 수 없는 유저 정보");
            }
            nameTextView.setText(user.getName());
            // 하이픈 넣고 싶으면 넣기
            phoneTextView.setText(user.getPhone());
        }
        Button yesBtn = view.findViewById(R.id.yes_button);
        Button noBtn = view.findViewById(R.id.no_button);
        yesBtn.setOnClickListener(new YesBtnListener());
        noBtn.setOnClickListener(new NoBtnListener());
        return view;
    }

    static class YesBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("ConnectInfoFragment", "Yes button clicked");
        }
    }

    static class NoBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}