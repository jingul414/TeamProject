package com.donghaeng.withme.data.worker;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkManagerInitializer {
    private static final long TEST_INTERVAL_MINUTES = 1;

    public static void startTestPeriodicWork(Context context) {
        Log.d("WorkManager", "Initializing periodic work");

        // WorkManager가 이미 실행 중인지 확인하고 취소
        WorkManager.getInstance(context).cancelAllWork();
        Log.d("WorkManager", "Cancelled existing work");

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(
                        BackgroundSettingsWorker.class,
                        TEST_INTERVAL_MINUTES,
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag("settings_sync")  // 태그 추가
                        .build();

        Log.d("WorkManager", "Created periodic work request");

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        "test_settings_sync",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWorkRequest
                );

        Log.d("WorkManager", "Enqueued periodic work");

        // 작업 상태 모니터링
        WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                .observeForever(workInfo -> {
                    if (workInfo != null) {
                        Log.d("WorkManager", "Work state: " + workInfo.getState());
                    }
                });
    }

    public static void stopTestPeriodicWork(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
    }
}