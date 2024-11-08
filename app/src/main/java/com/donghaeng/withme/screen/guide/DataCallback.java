package com.donghaeng.withme.screen.guide;

import java.util.List;

// 인터페이스
public interface DataCallback {
    void onDataLoaded(List<String> data);
    void onError(String error);  // 에러 처리 추가
}
