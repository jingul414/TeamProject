package com.donghaeng.withme.login.connect.target;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.donghaeng.withme.data.database.firestore.TokenManager;
import com.donghaeng.withme.login.connect.LocalConfirmationStatus;
import com.donghaeng.withme.login.connect.controller.NearbyHandler;
import com.donghaeng.withme.login.connect.message.ConfirmationPayload;
import com.donghaeng.withme.login.connect.message.NearbyMessage;
import com.donghaeng.withme.login.connect.message.TextMessagePayload;
import com.donghaeng.withme.login.connect.message.UserPayload;
import com.donghaeng.withme.screen.start.connect.TargetConnectFragment;
import com.donghaeng.withme.screen.start.connect.TargetQrFragment;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.User;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class AdvertisementHandler extends NearbyHandler {
    private String data;
    private final TargetConnect connect;

    @SuppressLint("StaticFieldLeak")
    private static AdvertisementHandler instance;

    private AdvertisementHandler(Fragment fragment) {
        super(fragment);
        this.connect = ((TargetQrFragment)fragment).getConnect();
    }

    public static synchronized AdvertisementHandler newInstance(Fragment fragment) {
        if (instance == null) {
            instance = new AdvertisementHandler(fragment);
        }
        return instance;
    }

    public static AdvertisementHandler getInstance() {
        return instance;
    }

    public void setAdvertiser() {
        if (hasPermissions(mFragment.requireContext())) {
            startAdvertising();
        } else {
            connect.checkPermissions();
        }
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
            TextMessagePayload payload = new TextMessagePayload(data);
            NearbyMessage message = new NearbyMessage("QR_ORIGIN", payload);
            String jsonMessage = new Gson().toJson(message);
            send(Payload.fromBytes(jsonMessage.getBytes()), endpointId);
            logD("Sent data: " + data + " to endpoint: " + endpointId);
        } else {
            logW("No data to send upon connection.");
        }
        stopAdvertising();
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
        // 수신된 데이터 처리
        Gson gson = new Gson();
        NearbyMessage message = gson.fromJson(data, NearbyMessage.class);
        Type type;
        switch (message.getType()) {
            case "OPPONENT_USER":
                type = new TypeToken<UserPayload>() {}.getType();
                UserPayload opponent = gson.fromJson(gson.toJson(message.getPayload()), type);
                receiveOpponent(endpointId, opponent);
                break;
            case "CONNECT_CONFIRMATION":
                type = new TypeToken<ConfirmationPayload>() {}.getType();
                ConfirmationPayload confirmationPayload = gson.fromJson(gson.toJson(message.getPayload()), type);                // 상대방 확인 상태 저장
                // 상대방 확인 상태 저장
                LocalConfirmationStatus.updateStatus(confirmationPayload.getUserId(), confirmationPayload.isConfirmation());
                // 상태 확인 후 다음 단계로 이동
                ((TargetQrFragment) mFragment).checkAndProceed(mOpponent);
                break;
            default:
                break;
        }
    }
    private Controller mOpponent;
    private void receiveOpponent(String endpointId, UserPayload data) {
        logD("Performing some action based on received data.");
        // 먼저 보내기
        sendMyInformation(endpointId);

        // 데이터 처리
        User tempUser = data.getUser();
        Controller opponent = new Controller(tempUser.getName(), tempUser.getPhone(), tempUser.getId(), "");
        opponent.setToken(tempUser.getToken());
        mOpponent = opponent;
        TargetConnectFragment nextFragment = (TargetConnectFragment) mFragment.getParentFragment();
        if (nextFragment != null) {
            nextFragment.setOpponentUserInfo(opponent);
            nextFragment.changeFragment("info");
        }
    }

    private void sendMyInformation(String endpointId) {
        logD("Performing some action based on received data.");
        // JSON
        User user = ((TargetQrFragment) mFragment).getUser();
        user.setToken(TokenManager.getInstance().getToken());
        UserPayload payload = new UserPayload(user);
        NearbyMessage message = new NearbyMessage("OPPONENT_USER", payload);
        String jsonMessage = new Gson().toJson(message);
        send(Payload.fromBytes(jsonMessage.getBytes()), endpointId);
    }

    public void clear() {
        stopAdvertising();
        mConnectionsClient.stopAllEndpoints();
        mConnectionsClient.stopDiscovery();
        mConnectionsClient.stopAdvertising();
        disconnectFromAllEndpoints();
        stopAllEndpoints();
        mDiscoveredEndpoints.clear();

        instance = null;
    }
}
