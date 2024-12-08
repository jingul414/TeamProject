package com.donghaeng.withme.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.donghaeng.withme.data.message.firebasemessage.MyFirebaseMessagingService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // FCM 토큰 새로고침 등 필요한 초기화 작업
            Intent refreshIntent = new Intent(context, MyFirebaseMessagingService.class);
            context.startService(refreshIntent);
        }
    }
}