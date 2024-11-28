package com.donghaeng.withme.screen.start.connect;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.login.connect.DiscoveryHandler;
import com.donghaeng.withme.login.connect.NearbyHandler;
import com.donghaeng.withme.login.connect.QRCodeGenerator;
import com.donghaeng.withme.login.connect.QRCodeReader;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;

@ExperimentalGetImage
public class ControllerQrFragment extends Fragment {
    private PreviewView viewFinder;

    private ControllerConnectFragment connectFragment;

    // Discovery
    private ConnectionsClient connectionsClient;
    private DiscoveryHandler discoveryHandler;

    // QR Reader
    private QRCodeReader qrCodeReader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controller_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectFragment = (ControllerConnectFragment) getParentFragment();

        viewFinder = view.findViewById(R.id.viewFinder);
        viewFinder.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
        // DiscoveryHandler 설정
        discoveryHandler = new DiscoveryHandler(this, connectionsClient);
        // QR 코드만을 위한 스캐너 설정
        qrCodeReader = new QRCodeReader(this, viewFinder, connectFragment, discoveryHandler);
        qrCodeReader.setScannerForQRcode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        NearbyHandler.checkPermissions(this);
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

//                    requireActivity().finish();
                    // 여기에서 뒤로 가기 동작을 수행합니다.
                    requireActivity().getSupportFragmentManager().popBackStack();
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
        discoveryHandler.stopDiscovering();
        qrCodeReader.cleanupCamera();
    }
}
