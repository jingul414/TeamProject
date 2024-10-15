package com.donghaeng.withme.background;

import android.content.Context;  // Context 사용 시 필요
import androidx.annotation.NonNull;  // @NonNull 어노테이션 사용 시 필요
import androidx.work.Worker;  // Worker 클래스
import androidx.work.WorkerParameters;  // Worker 생성자에 필요한 매개변수
import androidx.work.WorkManager;  // WorkManager 클래스
import androidx.work.OneTimeWorkRequest;  // One-time 작업 요청 시 필요
import androidx.work.WorkRequest;  // WorkRequest 사용 시 필요

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 백그라운드 작업을 수행
        performBackgroundTask();

        // 작업이 성공적으로 끝났을 경우
        return Result.success();
    }

    private void performBackgroundTask() {
        // 시간이 오래 걸리는 작업
    }
}

