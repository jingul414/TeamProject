package com.donghaeng.withme.myscreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.donghaeng.withme.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {

    Button signup_button;
    Button login_button;
    StartActivity startActivity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(String param1, String param2) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);
        signup_button = view.findViewById(R.id.signup_buuton);
        login_button = view.findViewById(R.id.login_button);

        startActivity = (StartActivity) requireActivity();

        signup_button.setOnClickListener(v -> {
            startActivity.changeFragment("signUp");
        });
        login_button.setOnClickListener(v -> {
            startActivity.changeFragment("login");
        });

        return view;
    }
}