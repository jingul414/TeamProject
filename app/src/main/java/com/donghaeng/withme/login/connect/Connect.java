package com.donghaeng.withme.login.connect;

import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public abstract class Connect {
    protected final Fragment mFragment;

    protected Connect(Fragment fragment){
        this.mFragment = fragment;
    }

    protected abstract void checkPermissions();

    protected boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(mFragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
