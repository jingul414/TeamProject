package com.donghaeng.withme.login.connect.target;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.login.connect.Connect;
import com.donghaeng.withme.login.connect.controller.NearbyHandler;
import com.donghaeng.withme.screen.start.connect.TargetQrFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetConnect extends Connect {
    private final AdvertisementHandler mHandler;
    private final QRCodeGenerator mGenerator;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;

    public TargetConnect(Fragment fragment) {
        super(fragment);
        mHandler = AdvertisementHandler.newInstance(fragment);
        mGenerator = new QRCodeGenerator(fragment, this);
        requestPermissionsLauncher = ((TargetQrFragment)mFragment).getRequestPermissionsLauncher();
    }

    @Override
    public void checkPermissions() {
        /* 필요한 모든 권한 기록 */
        List<String> requiredPermissions = new ArrayList<>(Arrays.asList(NearbyHandler.getRequiredPermissions()));

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

    public QRCodeGenerator getGenerator() {
        return mGenerator;
    }

    public AdvertisementHandler getHandler() {
        return mHandler;
    }
    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return requestPermissionsLauncher;
    }
}
