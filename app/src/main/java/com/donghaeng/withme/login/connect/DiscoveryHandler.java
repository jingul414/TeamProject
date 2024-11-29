package com.donghaeng.withme.login.connect;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.screen.start.connect.ControllerConnectFragment;
import com.donghaeng.withme.screen.start.connect.ControllerQrFragment;
import com.donghaeng.withme.user.Controller;
import com.donghaeng.withme.user.Target;
import com.donghaeng.withme.user.User;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.gson.Gson;

import org.mindrot.jbcrypt.BCrypt;

public class DiscoveryHandler extends NearbyHandler {
    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, DiscoveredEndpointInfo info) {
            // 발견된 기기를 처리
            logD(
                    String.format(
                            "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                            endpointId, info.getServiceId(), info.getEndpointName()));
            Toast.makeText(mContext, "기기 발견: " + info.getEndpointName(), Toast.LENGTH_SHORT).show();

            if (SERVICE_ID.equals(info.getServiceId())) {
                Endpoint endpoint = new Endpoint(endpointId, info.getEndpointName());
                mDiscoveredEndpoints.put(endpointId, endpoint);
                onEndpointDiscovered(endpoint);
            }
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            // 검색된 기기를 더 이상 찾을 수 없을 때 처리
            logD(String.format("onEndpointLost(endpointId=%s)", endpointId));
            Toast.makeText(mContext, "기기 연결 해제: " + endpointId, Toast.LENGTH_SHORT).show();
        }
    };
    private String mData;

    public DiscoveryHandler(Fragment fragment, ConnectionsClient client) {
        super(fragment, client);
    }

    /** 검색 모드 시작 (성공 시 onDiscoveryStarted, 실패 시 onDiscoveryFailed 호출) */
    public void startDiscovering(String data) {
        mData = data;
        if (isDiscovering) {
            Toast.makeText(mContext, "이미 기기를 검색 중입니다.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, "기기 검색을 시작합니다.", Toast.LENGTH_SHORT).show();
                            onDiscoveryStarted();
                        })
                .addOnFailureListener(
                        e -> {
                            // TODO: 기기에 GPS 기능이 꺼져 있다면, 기기 실패 => GPS 기능을 키도록 사용자에게 안내하는 알고리즘 필요
                            isDiscovering = false;
                            logW("startDiscovering() failed.", e);
                            Toast.makeText(mContext, "기기 검색 실패", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, "기기 검색을 중지합니다.", Toast.LENGTH_SHORT).show();
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

    public void requestConnection(String endpointId) {
        Nearby.getConnectionsClient(mContext)
                .requestConnection(getUserName(), endpointId, mConnectionLifecycleCallback)
                .addOnSuccessListener(unused -> {
                    Log.d("DiscoveryHandler", "연결 요청 성공: " + endpointId);
                    Toast.makeText(mContext, "연결 요청 성공", Toast.LENGTH_SHORT).show();
                    // TODO: 성공했을 때 처리

                })
                .addOnFailureListener(e -> {
                    Log.e("DiscoveryHandler", "연결 요청 실패: " + endpointId, e);
                    Toast.makeText(mContext, "연결 요청 실패", Toast.LENGTH_SHORT).show();
                });
    }
    /**
     * 엔드포인트 발견 시 호출되는 메소드
     */
    protected void onEndpointDiscovered(Endpoint endpoint) {
        // 연결 요청을 보낼 수 있음
        requestConnection(endpoint.getId());
    }

    @Override
    protected void onDataReceived(String endpointId, String data) {
        super.onDataReceived(endpointId, data);

        logD("DiscoveryHandler received data from endpoint: " + endpointId + ", data: " + data);

        // 수신된 데이터 처리 로직 추가
        Toast.makeText(mContext, "Received: " + data, Toast.LENGTH_SHORT).show();

        if (BCrypt.checkpw(data, mData)) {
            Toast.makeText(mContext, "데이터 일치", Toast.LENGTH_LONG).show();
            logD("데이터 일치");
            sendUserInfo(endpointId);
        }else{
            processInformation(data);
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void sendUserInfo(String endpointId) {
        logD("Performing some action based on received data.");
        // JSON
        Gson gson = new Gson();
        User user = ((ControllerQrFragment)mFragment).getUser();
        String userInfo = gson.toJson(user);
        send(Payload.fromBytes(userInfo.getBytes()), endpointId);
    }
    private void processInformation(String data) {
        logD("Performing some action based on received data.");
        // 데이터 처리
        Gson gson = new Gson();
        User tempUser = gson.fromJson(data, Target.class);
        Target opponent = new Target(tempUser.getName(), tempUser.getPhone(), tempUser.getId(), "");

        ControllerConnectFragment nextFragment = (ControllerConnectFragment) mFragment.getParentFragment();
        nextFragment.setOpponentUserInfo(opponent);
        nextFragment.changeFragment("info");
    }
}
