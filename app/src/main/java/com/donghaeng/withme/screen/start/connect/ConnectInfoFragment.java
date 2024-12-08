package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.connect.controller.NearbyHandler;
import com.donghaeng.withme.data.message.nearbymessage.ConfirmationPayload;
import com.donghaeng.withme.login.connect.LocalConfirmationStatus;
import com.donghaeng.withme.data.message.nearbymessage.NearbyMessage;
import com.donghaeng.withme.login.connect.target.AdvertisementHandler;
import com.donghaeng.withme.login.connect.controller.DiscoveryHandler;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.google.android.gms.nearby.connection.Payload;
import com.google.gson.Gson;

import java.util.Locale;


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
    private NearbyHandler handler;

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_info, container, false);

        connectFragment = getParentFragment();

        TextView infoTextView = view.findViewById(R.id.info_text);
        TextView nameTextView = view.findViewById(R.id.tel_name);
        TextView phoneTextView = view.findViewById(R.id.tel_text);
        if (opponent != null) {
            switch (opponent.getUserType()) {
                case UserType.CONTROLLER:
                    infoTextView.setText("보호자 정보");
                    handler = AdvertisementHandler.getInstance();
                    break;
                case UserType.TARGET:
                    infoTextView.setText("동행인 정보");
                    handler = DiscoveryHandler.getInstance();
                    break;
                default:
                    infoTextView.setText("알 수 없는 유저 정보");
                    handler = null;
            }
            nameTextView.setText(opponent.getName());
            // 하이픈 넣고 싶으면 넣기
            phoneTextView.setText(PhoneNumberUtils.formatNumber(opponent.getPhone(), Locale.getDefault().getCountry()));
        }
        Button yesBtn = view.findViewById(R.id.yes_button);
        yesBtn.setOnClickListener(new YesBtnListener());
        // 아니오 버튼 클릭시 뒤로 이동
        Button noBtn = view.findViewById(R.id.no_button);
        noBtn.setOnClickListener(v -> {
            if (opponent.getUserType() == UserType.CONTROLLER) {
                if (handler != null) {
                    // Advertisement 종료
                    AdvertisementHandler advertisementHandler = (AdvertisementHandler) handler;
                    advertisementHandler.clear();
                }
                // 상위 프래그먼트가 TargetConnectFragment인 경우
                if (connectFragment instanceof TargetConnectFragment) {
                    connectFragment.getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment, TargetQrFragment.newInstance(user))
                            .commit();
                }
            } else if (opponent.getUserType() == UserType.TARGET) {
                if (handler != null) {
                    // Discovery 종료
                    DiscoveryHandler discoveryHandler = (DiscoveryHandler) handler;
                    discoveryHandler.clear();
                }
                // 상위 프래그먼트가 ControllerConnectFragment인 경우
                if (connectFragment instanceof ControllerConnectFragment) {
                    connectFragment.getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment, ControllerQrFragment.newInstance(user))
                            .commit();
                }
            }
        });
        return view;
    }

    class YesBtnListener implements View.OnClickListener {
        @OptIn(markerClass = ExperimentalGetImage.class)
        @Override
        public void onClick(View v) {
            Log.d("ConnectInfoFragment", "Yes button clicked");
            /* 메세지 생성 */
            ConfirmationPayload payload = new ConfirmationPayload(user.getId(), true);
            NearbyMessage message = new NearbyMessage("CONNECT_CONFIRMATION", payload);
            String jsonMessage = new Gson().toJson(message);

            if (handler != null) {
                /* 메세지 전송 */
                handler.send(Payload.fromBytes(jsonMessage.getBytes()));
                /* 로컬 상태 업데이트 */
                LocalConfirmationStatus.updateStatus(user.getId(), true);

                /* 상태 확인 후 다음 단계로 이동 */
                if (getOpponent().getUserType() == UserType.TARGET) {
                    ((ControllerQrFragment) handler.getFragment()).checkAndProceed(getOpponent());
                } else if (getOpponent().getUserType() == UserType.CONTROLLER) {
                    ((TargetQrFragment) handler.getFragment()).checkAndProceed(getOpponent());
                }
            }
        }
    }

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }
}