package com.donghaeng.withme.screen.start.connect;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.connect.AdvertisementHandler;
import com.donghaeng.withme.login.connect.NearbyHandler;
import com.donghaeng.withme.login.connect.QRCodeGenerator;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;

/*  피제어자 => QR 생성  */
public class TargetQrFragment extends Fragment {
    private ImageView qrCodeImageView;

    // Advertise
    private ConnectionsClient connectionsClient;
    private AdvertisementHandler advertisementHandler;

    // QR Generator
    private QRCodeGenerator qrCodeGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_qr, container, false);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        advertisementHandler.stopAdvertising();
    }

    /**
     * Fragment가 처음 생성될 때 호출
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionsClient = Nearby.getConnectionsClient(requireActivity());
    }

    /**
     * Fragment가 사용자에게 보이게 되었을 때 호출
     */
    @Override
    public void onStart() {
        super.onStart();

        // 권한 받기
        NearbyHandler.checkPermissions(this);

        advertisementHandler = new AdvertisementHandler(this, connectionsClient);
        advertisementHandler.startAdvertising();

        qrCodeGenerator = new QRCodeGenerator(this, advertisementHandler);
        qrCodeGenerator.generateQRCode(qrCodeImageView);
    }

    /**
     * 사용자가 권한 요청을 수락(또는 거부)했을 때 호출
     */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == NearbyHandler.REQUEST_CODE_REQUIRED_PERMISSIONS) {
            int i = 0;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Log.w("TargetQRFragment","Permissions not granted"+permissions[i]);
                    Toast.makeText(this.requireContext(), "Permissions not granted", Toast.LENGTH_LONG).show();

                    requireActivity().finish();
                    return;
                }
                i++;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        advertisementHandler.stopAdvertising();
    }
}