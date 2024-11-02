package com.donghaeng.withme.myscreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.donghaeng.withme.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupStep1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupStep1Fragment extends Fragment {

    Button next_button;
    StartActivity startActivity;
    String sign_name = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupStep1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupStep1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupStep1Fragment newInstance(String param1, String param2) {
        SignupStep1Fragment fragment = new SignupStep1Fragment();
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
        View view = inflater.inflate(R.layout.fragment_signup_step1, container, false);

        next_button = view.findViewById(R.id.btn_next);
        startActivity = (StartActivity) requireActivity();
        next_button.setOnClickListener(v -> {
            EditText editText = view.findViewById(R.id.number);
            sign_name = editText.getText().toString();
            startActivity.setSignName(sign_name);
            startActivity.changeFragment("step2");
        });

        return view;
    }
}