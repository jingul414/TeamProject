package com.donghaeng.withme.login.connect;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.donghaeng.withme.screen.start.connect.TargetConnectFragment;
import com.donghaeng.withme.screen.start.connect.TargetQrFragment;
import com.donghaeng.withme.user.Controller;
import com.donghaeng.withme.user.Target;
import com.donghaeng.withme.user.User;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AdvertisementHandler extends NearbyHandler {
    private String data;

    public AdvertisementHandler(Fragment fragment, ConnectionsClient client) {
        super(fragment, client);
    }

    /**
     * 광고 모드 시작 (성공 시 onAdvertisingStarted, 실패 시 onAdvertisingFailed 호출)
     */
    public void startAdvertising() {
        if (isAdvertising) {
            Toast.makeText(mContext, "이미 광고가 실행 중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        isAdvertising = true;
        final String localEndpointName = getUserName();

        AdvertisingOptions.Builder advertisingOptions = new AdvertisingOptions.Builder();
        advertisingOptions.setStrategy(Strategy.P2P_POINT_TO_POINT);

        mConnectionsClient
                .startAdvertising(
                        localEndpointName,
                        SERVICE_ID,
                        mConnectionLifecycleCallback,
                        advertisingOptions.build())
                .addOnSuccessListener(
                        (Void unused) -> {
                            logV("Now advertising endpoint " + localEndpointName);
                            Toast.makeText(mContext, "광고 시작 성공", Toast.LENGTH_SHORT).show();
                            onAdvertisingStarted();
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            isAdvertising = false;
                            logW("startAdvertising() failed.", e);
                            Toast.makeText(mContext, "광고 시작 실패", Toast.LENGTH_SHORT).show();
                            onAdvertisingFailed();
                        });
    }

    /**
     * 광고 중단
     */
    public void stopAdvertising() {
        if (isAdvertising) {
            isAdvertising = false;
            mConnectionsClient.stopAdvertising();
            Toast.makeText(mContext, "광고 중지됨", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 광고 시작 성공 시 호출되는 메소드
     */
    private void onAdvertisingStarted() {
    }

    /**
     * 광고 시작 실패 시 호출되는 메소드
     */
    private void onAdvertisingFailed() {
    }

    @Override
    public void onSuccessfulConnection(String endpointId) {
        super.onSuccessfulConnection(endpointId);

        // 연결 성공 시 데이터 전송
        if (data != null) {
            send(Payload.fromBytes(data.getBytes()), endpointId);
            logD("Sent data: " + data + " to endpoint: " + endpointId);
        } else {
            logW("No data to send upon connection.");
        }
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    protected void onDataReceived(String endpointId, String data) {
        super.onDataReceived(endpointId, data);

        logD("DiscoveryHandler received data from endpoint: " + endpointId + ", data: " + data);

        // 수신된 데이터 처리 로직 추가
        Toast.makeText(mContext, "Received: " + data, Toast.LENGTH_SHORT).show();

        processInformation(endpointId, data);
    }

    private void processInformation(String endpointId, String data) {
        logD("Performing some action based on received data.");
        // 추가 동작 구현
        Gson gson = new Gson();

        // 먼저 보내기
        sendUserInfo(endpointId);

        // 데이터 처리
        User tempUser = gson.fromJson(data, Controller.class);
        Controller opponent = new Controller(tempUser.getName(), tempUser.getPhone(), tempUser.getId(), "");

        TargetConnectFragment nextFragment = (TargetConnectFragment) mFragment.getParentFragment();
        nextFragment.setOpponentUserInfo(opponent);
        nextFragment.changeFragment("info");
    }

    private void sendUserInfo(String endpointId) {
        logD("Performing some action based on received data.");
        // JSON
        Gson gson = new Gson();
        User user = ((TargetQrFragment)mFragment).getUser();
        String userInfo = gson.toJson(user);
        send(Payload.fromBytes(userInfo.getBytes()), endpointId);
    }
}
