package com.donghaeng.withme.login.connect.controller;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.data.database.firestore.TokenManager;
import com.donghaeng.withme.login.connect.LocalConfirmationStatus;
import com.donghaeng.withme.data.message.nearbymessage.ConfirmationPayload;
import com.donghaeng.withme.data.message.nearbymessage.NearbyMessage;
import com.donghaeng.withme.data.message.nearbymessage.TextMessagePayload;
import com.donghaeng.withme.data.message.nearbymessage.UserPayload;
import com.donghaeng.withme.screen.start.connect.ControllerConnectFragment;
import com.donghaeng.withme.screen.start.connect.ControllerQrFragment;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.mindrot.jbcrypt.BCrypt;

import java.lang.reflect.Type;

public class DiscoveryHandler extends NearbyHandler {
    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, DiscoveredEndpointInfo info) {
            // 발견된 기기를 처리
            logD(
                    String.format(
                            "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                            endpointId, info.getServiceId(), info.getEndpointName()));

            if (mFragment.getActivity() != null) {
                mFragment.getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "기기 발견: " + info.getEndpointName(), Toast.LENGTH_SHORT).show();
                });
            }

            if (SERVICE_ID.equals(info.getServiceId())) {
                Endpoint endpoint = new Endpoint(endpointId, info.getEndpointName());
                mDiscoveredEndpoints.put(endpointId, endpoint);
                onEndpointDiscovered(endpoint);
            }
        }

        @OptIn(markerClass = ExperimentalGetImage.class)
        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            // 검색된 기기를 더 이상 찾을 수 없을 때 처리
            logD(String.format("onEndpointLost(endpointId=%s)", endpointId));
            if (mFragment.getActivity() != null) {
                mFragment.getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "기기 연결 해제: " + endpointId, Toast.LENGTH_SHORT).show();
                    if (mFragment instanceof ControllerQrFragment) {
                        ((ControllerQrFragment) mFragment).onConnectionFailed();
                    }
                });
            }
        }
    };
    private String mData;
    @SuppressLint("StaticFieldLeak")
    private static DiscoveryHandler instance;

    private DiscoveryHandler(Fragment fragment) {
        super(fragment);
    }

    public static synchronized DiscoveryHandler newInstance(Fragment fragment) {
        if (instance == null) {
            instance = new DiscoveryHandler(fragment);
        }
        return instance;
    }

    public static DiscoveryHandler getInstance() {
        return instance;
    }

    /**
     * 검색 모드 시작 (성공 시 onDiscoveryStarted, 실패 시 onDiscoveryFailed 호출)
     */
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void startDiscovering(String data) {
        mData = data;
        if (isDiscovering) {
            mFragment.getActivity().runOnUiThread(() -> {
                Toast.makeText(mContext, "이미 기기를 검색 중입니다.", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        isDiscovering = true;
        mDiscoveredEndpoints.clear();
        DiscoveryOptions.Builder discoveryOptions = new DiscoveryOptions.Builder();
        discoveryOptions.setStrategy(Strategy.P2P_POINT_TO_POINT);
        mConnectionsClient
                .startDiscovery(
                        SERVICE_ID,
                        mEndpointDiscoveryCallback,
                        discoveryOptions.build())
                .addOnSuccessListener(
                        unused -> {
                            logD("Now discovering");
                            mFragment.getActivity().runOnUiThread(() -> {
                                Toast.makeText(mContext, "기기 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                            });
                            onDiscoveryStarted();
                        })
                .addOnFailureListener(
                        e -> {
                            isDiscovering = false;
                            logW("startDiscovering() failed.", e);
                            mFragment.getActivity().runOnUiThread(() -> {
                                Toast.makeText(mContext, "기기 검색 실패", Toast.LENGTH_SHORT).show();
                                if (mFragment instanceof ControllerQrFragment) {
                                    ((ControllerQrFragment) mFragment).onConnectionFailed();
                                }
                            });
                            onDiscoveryFailed();
                        });
    }

    /**
     * 검색 중단
     */
    public void stopDiscovering() {
        if (isDiscovering) {
            isDiscovering = false;
            mConnectionsClient.stopDiscovery();
            logD("기기 검색 중지됨");
            if (mFragment.getActivity() != null) {
                mFragment.getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "기기 검색을 중지합니다.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    /**
     * 검색이 성공적으로 시작되었을 때 호출되는 메소드
     */
    protected void onDiscoveryStarted() {
    }

    /**
     * 검색 시작 실패 시 호출되는 메소드
     */
    protected void onDiscoveryFailed() {
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void requestConnection(String endpointId) {
        Nearby.getConnectionsClient(mContext)
                .requestConnection(getUserName(), endpointId, mConnectionLifecycleCallback)
                .addOnSuccessListener(unused -> {
                    Log.d("DiscoveryHandler", "연결 요청 성공: " + endpointId);
                    mFragment.getActivity().runOnUiThread(() -> {
                        Toast.makeText(mContext, "연결 요청 성공", Toast.LENGTH_SHORT).show();
                    });
                    stopDiscovering();
                })
                .addOnFailureListener(e -> {
                    Log.e("DiscoveryHandler", "연결 요청 실패: " + endpointId, e);
                    mFragment.getActivity().runOnUiThread(() -> {
                        Toast.makeText(mContext, "연결 요청 실패", Toast.LENGTH_SHORT).show();
                        if (mFragment instanceof ControllerQrFragment) {
                            ((ControllerQrFragment) mFragment).onConnectionFailed();
                        }
                    });
                });
    }

    /**
     * 엔드포인트 발견 시 호출되는 메소드
     */
    protected void onEndpointDiscovered(Endpoint endpoint) {
        // 연결 요청을 보낼 수 있음
        requestConnection(endpoint.getId());
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        super.onConnectionFailed(endpoint);
        if (mFragment instanceof ControllerQrFragment) {
            mFragment.getActivity().runOnUiThread(() -> {
                ((ControllerQrFragment) mFragment).onConnectionFailed();
            });
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    protected void onDataReceived(String endpointId, String data) {
        super.onDataReceived(endpointId, data);

        logD("DiscoveryHandler received data from endpoint: " + endpointId + ", data: " + data);

        // 수신된 데이터 처리
        Gson gson = new Gson();
        NearbyMessage message = gson.fromJson(data, NearbyMessage.class);
        Type type;
        switch (message.getType()) {
            case "QR_ORIGIN":
                type = new TypeToken<TextMessagePayload>() {}.getType();
                TextMessagePayload origin = gson.fromJson(gson.toJson(message.getPayload()), type);
                String text = origin.getMessage();
                if (BCrypt.checkpw(text, mData)) sendMyInformation(endpointId);
                break;
            case "OPPONENT_USER":
                type = new TypeToken<UserPayload>() {}.getType();
                UserPayload opponent = gson.fromJson(gson.toJson(message.getPayload()), type);
                receiveOpponent(opponent);
                break;
            case "CONNECT_CONFIRMATION":
                type = new TypeToken<ConfirmationPayload>() {}.getType();
                ConfirmationPayload confirmationPayload = gson.fromJson(gson.toJson(message.getPayload()), type);
                // 상대방 확인 상태 저장
                LocalConfirmationStatus.updateStatus(confirmationPayload.getUserId(), confirmationPayload.isConfirmation());
                // 상태 확인 후 다음 단계로 이동
                ((ControllerQrFragment)mFragment).checkAndProceed(mOpponent);
                break;
            default:
                break;
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void sendMyInformation(String endpointId) {
        logD("Performing some action based on received data.");
        // JSON
        User user = ((ControllerQrFragment) mFragment).getUser();
        UserPayload payload = new UserPayload(user);
        NearbyMessage message = new NearbyMessage("OPPONENT_USER", payload);
        String jsonMessage = new Gson().toJson(message);
        send(Payload.fromBytes(jsonMessage.getBytes()), endpointId);
    }

    private Target mOpponent;
    private void receiveOpponent(UserPayload data) {
        logD("Performing some action based on received data.");
        // 데이터 처리
        User tempUser = data.getUser();
        Target opponent = new Target(tempUser.getName(), tempUser.getPhone(), tempUser.getId(), "");
        opponent.setToken(tempUser.getToken());
        mOpponent = opponent;
        ControllerConnectFragment nextFragment = (ControllerConnectFragment) mFragment.getParentFragment();
        if (nextFragment != null) {
            mFragment.getActivity().runOnUiThread(() -> {
                nextFragment.setOpponentUserInfo(opponent);
                nextFragment.changeFragment("info");
            });
        }
    }

    @Override
    protected void onSuccessfulConnection(String endpointId) {
        super.onSuccessfulConnection(endpointId);
        // 연결 성공 시에는 로딩 UI를 계속 표시
        // 최종적으로 checkAndProceed에서 처리됨
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void clear() {
        stopDiscovering();
        mConnectionsClient.stopAllEndpoints();
        mConnectionsClient.stopDiscovery();
        mConnectionsClient.stopAdvertising();
        disconnectFromAllEndpoints();
        stopAllEndpoints();
        mDiscoveredEndpoints.clear();

        // 로딩 UI 숨김
        if (mFragment instanceof ControllerQrFragment && mFragment.getActivity() != null) {
            mFragment.getActivity().runOnUiThread(() ->
                    ((ControllerQrFragment) mFragment).hideLoading()
            );
        }

        instance = null;
    }
}