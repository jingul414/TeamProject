package com.donghaeng.withme.login.connect.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.login.connect.Connect;
import com.donghaeng.withme.screen.start.connect.ControllerQrFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExperimentalGetImage
public class ControllerConnect extends Connect {
    private final DiscoveryHandler mHandler;
    private final QRCodeReader mReader;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;

    public ControllerConnect(Fragment fragment) {
        super(fragment);
        mHandler = DiscoveryHandler.newInstance(fragment);
        mReader = new QRCodeReader(fragment, this);
        requestPermissionsLauncher = ((ControllerQrFragment)mFragment).getRequestPermissionsLauncher();
    }

    @Override
    public void checkPermissions() {
        /* 필요한 모든 권한 기록 */
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.addAll(Arrays.asList(QRCodeReader.getRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(NearbyHandler.getRequiredPermissions()));

        /* 필요한 권한 중 누락된 권한 기록 */
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if(!isGranted(permission)) {
                missingPermissions.add(permission);
            }
        }

        /* 누락된 권한 요청하기 */
        if(missingPermissions.isEmpty()) return;
        requestPermissionsLauncher.launch(missingPermissions.toArray(new String[0]));
    }

    public QRCodeReader getReader() {
        return mReader;
    }

    public DiscoveryHandler getHandler() {
        return mHandler;
    }
}
