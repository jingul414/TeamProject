package com.donghaeng.withme.screen.start.connect;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.firebasestore.FireStoreManager;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.user.Controller;
import com.donghaeng.withme.user.Target;
import com.donghaeng.withme.user.User;
import com.donghaeng.withme.user.UserType;


public class ConnectInfoFragment extends Fragment {
    /**
     * Fragment 생성자 데이터
     */
    private static final String ARG_USER = "user";
    private static final String ARG_OPPONENT = "opponent";
    private User user;
    private User opponent;

    public ConnectInfoFragment() {
        // Required empty public constructor
    }

    public static ConnectInfoFragment newInstance(User user, User opponent) {
        ConnectInfoFragment fragment = new ConnectInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_OPPONENT, opponent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
            opponent = getArguments().getParcelable(ARG_OPPONENT);
        }
    }

    private Fragment connectFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_info, container, false);
        // 오류 이유 => 보호자만 연결된거임
        connectFragment = getParentFragment();

        TextView infoTextView = view.findViewById(R.id.info_text);
        TextView nameTextView = view.findViewById(R.id.tel_name);
        TextView phoneTextView = view.findViewById(R.id.tel_text);
        if (opponent != null) {
            switch (opponent.getUserType()) {
                case UserType.CONTROLLER:
                    infoTextView.setText("보호자 정보");
                    break;
                case UserType.TARGET:
                    infoTextView.setText("동행인 정보");
                    break;
                default:
                    infoTextView.setText("알 수 없는 유저 정보");
            }
            nameTextView.setText(opponent.getName());
            // 하이픈 넣고 싶으면 넣기
            phoneTextView.setText(opponent.getPhone());
        }
        Button yesBtn = view.findViewById(R.id.yes_button);
        yesBtn.setOnClickListener(new YesBtnListener());
        // 아니오 버튼 클릭시 뒤로 이동
        View back = view.findViewById(R.id.no_button);
        back.setOnClickListener(v -> {
            switch (opponent.getUserType()) {
                case UserType.CONTROLLER:
                    ((TargetConnectFragment) connectFragment).changeFragment("qr");
                    break;
                case UserType.TARGET:
                    ((ControllerConnectFragment) connectFragment).changeFragment("qr");
                    break;
            }
        });
        return view;
    }

    class YesBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("ConnectInfoFragment", "Yes button clicked");
            // TODO: 상대 응답 기다리는 과정 필요


            // User 정보 firestore에 저장
            User undefinedUser = getUser();
            if (getOpponent().getUserType() == UserType.CONTROLLER) {
                user = new Target(undefinedUser.getName(), undefinedUser.getPhone(), undefinedUser.getId(), undefinedUser.getHashedPassword());
                ((Target) user).addController((Controller) getOpponent());
            } else if (getOpponent().getUserType() == UserType.TARGET) {
                user = new Controller(undefinedUser.getName(), undefinedUser.getPhone(), undefinedUser.getId(), undefinedUser.getHashedPassword());
                ((Controller) user).addTarget((Target) getOpponent());
            }
            FireStoreManager fireStoreManager = FireStoreManager.getInstance();
            fireStoreManager.updateUserData(user);

            //TODO: 로그인 프래그먼트로 이동
            Intent intent = new Intent(requireActivity(), StartActivity.class);
            intent.putExtra("fragmentName","LoginFragment");
            startActivity(intent);
        }
    }

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }
}