package com.donghaeng.withme.login.connect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class NearbyHandler {

    protected static final String TAG = "NearbyConnections";
    /**
     * Nearby Connections 연결에 필요한 권한
     */
    protected static final String[] REQUIRED_PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.NEARBY_WIFI_DEVICES,
                    };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    };
        } else {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    };
        }
    }

    public static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    /**
     * Nearby Connections 클라이언트
     */
    protected ConnectionsClient mConnectionsClient;
    /**
     * 발견된 기기들
     */
    protected final Map<String, NearbyHandler.Endpoint> mDiscoveredEndpoints = new HashMap<>();
    /**
     * acceptConnection 또는 rejectConnection 호출 전까지 연결 대기 중인 기기들
     */
    protected final Map<String, NearbyHandler.Endpoint> mPendingConnections = new HashMap<>();
    /**
     * 연결된 기기 목록 (광고자는 여러 기기 가능, 검색자는 한 기기만 가능)
     */
    protected final Map<String, Endpoint> mEstablishedConnections = new HashMap<>();
    /**
     * 연결 요청 중이면 true 반환 (다른 기기에 연결 요청 불가)
     */
    protected boolean isConnecting = false;
    /**
     * 현재 검색 중 여부
     */
    protected boolean isDiscovering = false;
    /**
     * 현재 광고 중 여부
     */
    protected boolean isAdvertising = false;
    /**
     * 다른 기기와의 연결에 대한 콜백
     */
    protected final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, ConnectionInfo connectionInfo) {
            logD(
                    String.format(
                            "onConnectionInitiated(endpointId=%s, endpointName=%s)",
                            endpointId, connectionInfo.getEndpointName()));
            Endpoint endpoint = new Endpoint(endpointId, connectionInfo.getEndpointName());
            mPendingConnections.put(endpointId, endpoint);
            NearbyHandler.this.onConnectionInitiated(endpoint, connectionInfo);
            if(!isAdvertising && !isDiscovering) return;
            acceptConnection(endpoint);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            logD(String.format("onConnectionResponse(endpointId=%s, result=%s)", endpointId, result));

            // We're no longer connecting
            isConnecting = false;

            if (!result.getStatus().isSuccess()) {
                logW(
                        String.format(
                                "Connection failed. Received status %s.",
                                NearbyHandler.toString(result.getStatus())));
                onConnectionFailed(mPendingConnections.remove(endpointId));
                return;
            }
            Endpoint endpoint = mPendingConnections.remove(endpointId);
            connectedToEndpoint(endpoint);

            // 하위 클래스에서 동작을 정의할 수 있도록 추가
            onSuccessfulConnection(endpointId);
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            if (!mEstablishedConnections.containsKey(endpointId)) {
                logW("Unexpected disconnection from endpoint " + endpointId);
                return;
            }
            disconnectedFromEndpoint(mEstablishedConnections.get(endpointId));
        }
    };
    /**
     * 다른 기기에서 받은 데이터(바이트)에 대한 콜백
     */
    protected final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            logD(String.format("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payload));
            onReceive(mEstablishedConnections.get(endpointId), payload);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
            logD(
                    String.format(
                            "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update));
        }
    };

    protected static final String SERVICE_ID = "com.donghaeng.withme.connect";
    protected final Fragment mFragment;
    protected final Context mContext;

    public NearbyHandler(Fragment fragment, ConnectionsClient connectionsClient) {
        mFragment = fragment;
        mContext = mFragment.requireContext();
        mConnectionsClient = connectionsClient;
    }

    public static void checkPermissions(Fragment fragment) {
        // TODO: getRequiredPermissions()가 static 이어도 괜찮은 지 확인 필요
        if (!hasPermissions(fragment.requireContext(), getRequiredPermissions())) {
            Toast.makeText(fragment.requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
            fragment.requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    /**
     * 연결 초기화 시 호출되는 메소드 (계속하려면 acceptConnection, 거부하려면 rejectConnection 호출)
     */
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
    }

    /**
     * 연결 요청 수락
     */
    protected void acceptConnection(final Endpoint endpoint) {
        mConnectionsClient
                .acceptConnection(endpoint.getId(), mPayloadCallback)
                .addOnFailureListener(
                        e -> logW("acceptConnection() failed.", e));
    }

    /**
     * 연결 요청 거부
     */
    protected void rejectConnection(Endpoint endpoint) {
        mConnectionsClient
                .rejectConnection(endpoint.getId())
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("rejectConnection() failed.", e);
                            }
                        });
    }

    /**
     * 지정된 엔드포인트와의 연결 해제
     */
    protected void disconnect(Endpoint endpoint) {
        mConnectionsClient.disconnectFromEndpoint(endpoint.getId());
        mEstablishedConnections.remove(endpoint.getId());
    }

    /**
     * 현재 연결된 모든 엔드포인트와의 연결 해제
     */
    protected void disconnectFromAllEndpoints() {
        for (Endpoint endpoint : mEstablishedConnections.values()) {
            mConnectionsClient.disconnectFromEndpoint(endpoint.getId());
        }
        mEstablishedConnections.clear();
    }

    /**
     * Nearby Connections의 모든 상태 초기화 및 정리
     */
    protected void stopAllEndpoints() {
        mConnectionsClient.stopAllEndpoints();
        isAdvertising = false;
        isDiscovering = false;
        isConnecting = false;
        mDiscoveredEndpoints.clear();
        mPendingConnections.clear();
        mEstablishedConnections.clear();
    }

    /**
     * 엔드포인트 연결 요청 (성공 시 onConnectionInitiated, 실패 시 onConnectionFailed 호출)
     */
    protected void connectToEndpoint(final Endpoint endpoint) {
        logV("Sending a connection request to endpoint " + endpoint);
        // 중복 연결 방지
        isConnecting = true;

        // 연결 요청
        mConnectionsClient
                .requestConnection(getUserName(), endpoint.getId(), mConnectionLifecycleCallback)
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("requestConnection() failed.", e);
                                isConnecting = false;
                                onConnectionFailed(endpoint);
                            }
                        });
    }

    /**
     * 현재 다른 기기에 연결 시도 중 여부
     */
    protected final boolean isConnecting() {
        return isConnecting;
    }

    private void connectedToEndpoint(Endpoint endpoint) {
        logD(String.format("connectedToEndpoint(endpoint=%s)", endpoint));
        mEstablishedConnections.put(endpoint.getId(), endpoint);
        onEndpointConnected(endpoint);
    }

    private void disconnectedFromEndpoint(Endpoint endpoint) {
        logD(String.format("disconnectedFromEndpoint(endpoint=%s)", endpoint));
        mEstablishedConnections.remove(endpoint.getId());
        onEndpointDisconnected(endpoint);
    }

    /**
     * 엔드포인트와의 연결이 실패했을 때 호출되는 메소드
     */
    protected void onConnectionFailed(Endpoint endpoint) {
    }

    /**
     * 누군가 연결되었을 때 호출되는 메소드
     */
    protected void onEndpointConnected(Endpoint endpoint) {
    }

    /**
     * 누군가 연결을 해제했을 때 호출되는 메소드
     */
    protected void onEndpointDisconnected(Endpoint endpoint) {
    }

    /**
     * 현재 발견된 엔드포인트 목록 반환
     */
    protected Set<Endpoint> getDiscoveredEndpoints() {
        return new HashSet<>(mDiscoveredEndpoints.values());
    }

    /**
     * 현재 연결된 엔드포인트 목록 반환
     */
    protected Set<Endpoint> getConnectedEndpoints() {
        return new HashSet<>(mEstablishedConnections.values());
    }

    /**
     * 현재 연결된 모든 엔드포인트에 Payload 전송
     *
     * @param payload 전송할 데이터
     */
    protected void send(Payload payload) {
        send(payload, mEstablishedConnections.keySet());
    }
    protected void send(Payload payload, String endpointId) {
        mConnectionsClient
                .sendPayload(endpointId, payload)
                .addOnFailureListener(e -> logW("Failed to send payload to endpoint: " + endpointId, e))
                .addOnSuccessListener(unused -> logD("Payload sent to endpoint: " + endpointId));
    }

    private void send(Payload payload, Set<String> endpoints) {
        mConnectionsClient
                .sendPayload(new ArrayList<>(endpoints), payload)
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("sendPayload() failed.", e);
                            }
                        });
    }

    /**
     * 데이터 수신 시 호출되는 메소드
     *
     * @param endpoint 보낸 사람
     * @param payload  데이터
     */
    protected void onReceive(Endpoint endpoint, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            String receivedData = new String(payload.asBytes());
            logD("Received data: " + receivedData);

            // 데이터를 하위 클래스에서 처리하도록 호출
            onDataReceived(endpoint.getId(), receivedData);
        } else {
            logW("Unsupported payload type received");
        }
    }
    /**
     * 데이터 수신 시 호출되는 메서드 (하위 클래스에서 재정의 가능)
     * @param endpointId 데이터가 전송된 엔드포인트 ID
     * @param data 수신된 String 데이터
     */
    protected void onDataReceived(String endpointId, String data) {
        logD("Data received from endpoint: " + endpointId + ", data: " + data);
        // 하위 클래스에서 재정의하여 데이터 처리
    }

    /**
     * 필요한 모든 권한 반환
     * @return 앱이 정상 동작하기 위해 필요한 모든 권한
     */
    protected static String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    /**
     * Status를 읽기 쉬운 메시지로 변환
     *
     * @param status 현재 상태
     * @return 읽을 수 있는 문자열. 예: [404]File not found.
     */
    private static String toString(Status status) {
        return String.format(
                Locale.US,
                "[%d]%s",
                status.getStatusCode(),
                status.getStatusMessage() != null
                        ? status.getStatusMessage()
                        : ConnectionsStatusCodes.getStatusCodeString(status.getStatusCode()));
    }

    /**
     * 모든 권한 부여 여부 반환
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected String getUserName() {
        return Build.BRAND + "의 " + Build.MODEL;
    }


    @CallSuper
    protected void logV(String msg) {
        Log.v(TAG, msg);
    }

    @CallSuper
    protected void logD(String msg) {
        Log.d(TAG, msg);
    }

    @CallSuper
    protected void logW(String msg) {
        Log.w(TAG, msg);
    }

    @CallSuper
    protected void logW(String msg, Throwable e) {
        Log.w(TAG, msg, e);
    }

    @CallSuper
    protected void logE(String msg, Throwable e) {
        Log.e(TAG, msg, e);
    }

    /**
     * 연결 성공 시 호출되는 메서드 (하위 클래스에서 재정의 가능)
     */
    protected void onSuccessfulConnection(String endpointId) {
        // 기본적으로 아무 작업도 하지 않음
        logD("Connection succeeded with endpoint: " + endpointId);
    }

    /**
     * 통신할 수 있는 기기를 나타내는 클래스
     */
    protected static class Endpoint {
        @NonNull
        private final String id;
        @NonNull
        private final String name;

        protected Endpoint(@NonNull String id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Endpoint) {
                Endpoint other = (Endpoint) obj;
                return id.equals(other.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Endpoint{id=%s, name=%s}", id, name);
        }
    }
}
