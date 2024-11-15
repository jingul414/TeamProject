package com.donghaeng.withme.screen.guide;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private static DataRepository instance;
    private Map<String, List<String>> cache;  // 데이터 캐싱을 위한 맵

    private DataRepository() {
        cache = new HashMap<>();
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void loadSubItems(String headerId, final DataCallback callback) {
        // 캐시된 데이터가 있는지 확인
        if (cache.containsKey(headerId)) {
            callback.onDataLoaded(cache.get(headerId));
            return;
        }

        new LoadDataTask(headerId, callback, cache).execute();
    }

    // Static 내부 클래스로 분리
    private static class LoadDataTask extends AsyncTask<Void, Void, List<String>> {
        private final String headerId;
        private final DataCallback callback;
        private final Map<String, List<String>> cache;
        private String error = null;

        LoadDataTask(String headerId, DataCallback callback, Map<String, List<String>> cache) {
            this.headerId = headerId;
            this.callback = callback;
            this.cache = cache;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> subItems = new ArrayList<>();
            try {
                // 실제 데이터 로딩 로직 - 현재는 임시적으로 아이템 지정함
                // ToDo 여기서 제어자가 설정한 가이드 부분 서버에서 불러오는 코드 만들면 됨.
                switch (headerId) {
                    case "guide":
                        subItems.addAll(Arrays.asList(
                                "가이드 1장: 기본 소개",
                                "가이드 2장: 상세 설명",
                                "가이드 3장: 실전 활용",
                                "가이드 4장: 문제 해결",
                                "가이드 5장: 고급 기능"
                        ));
                        break;
                    case "smartphone":
                        subItems.addAll(Arrays.asList(
                                "스마트폰 설정하기",
                                "앱 설치 방법",
                                "보안 설정 방법",
                                "백업 및 복원",
                                "문제 해결 가이드"
                        ));
                        break;
                    case "guardian":
                        subItems.addAll(Arrays.asList(
                                "보호자 권한 설정",
                                "모니터링 방법",
                                "긴급 상황 대처법",
                                "제한 설정 방법",
                                "활동 기록 확인"
                        ));
                        break;
                    default:
                        error = "Unknown header ID: " + headerId;
                        return null;
                }

                // 네트워크 지연 시뮬레이션
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                error = "데이터 로딩 중 오류가 발생했습니다";
                return null;
            }

            return subItems;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (error != null) {
                callback.onError(error);
            } else {
                // 결과를 캐시에 저장
                cache.put(headerId, result);
                callback.onDataLoaded(result);
            }
        }
    }

    // 캐시 초기화 메서드
    public void clearCache() {
        cache.clear();
    }
}