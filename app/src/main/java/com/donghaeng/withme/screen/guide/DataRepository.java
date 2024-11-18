package com.donghaeng.withme.screen.guide;

import android.os.AsyncTask;
import android.util.Log;

import com.donghaeng.withme.roomdatabase.guide.GuideBook;
import com.donghaeng.withme.roomdatabase.guide.GuideBookRepository;
import com.donghaeng.withme.roomdatabase.guide.GuideBookType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private static DataRepository instance;
    private Map<String, List<String>> cache;  // 데이터 캐싱을 위한 맵

    private GuideActivity guideActivity;

    private DataRepository() {
        cache = new HashMap<>();
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void loadSubItems(String headerId, GuideActivity guideActivity,final DataCallback callback) {
        this.guideActivity = guideActivity;
        // 캐시된 데이터가 있는지 확인
        if (cache.containsKey(headerId)) {
            callback.onDataLoaded(cache.get(headerId));
            return;
        }

        new LoadDataTask(headerId, callback, cache).execute();
    }

    public GuideActivity getGuideActivity() {
        return guideActivity;
    }

    // Static 내부 클래스로 분리
    private static class LoadDataTask extends AsyncTask<Void, Void, List<String>> {
        private final String headerId;
        private final DataCallback callback;
        private final Map<String, List<String>> cache;
        private String error = null;

        private GuideActivity guideActivity;
        private GuideBookRepository guideBookRepository;

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
                guideActivity = getInstance().getGuideActivity();
                guideBookRepository = new GuideBookRepository(guideActivity);
                List<String> titles = new ArrayList<>();

                switch (headerId) {
                    case "guide":
                        for (GuideBook guideBook : guideBookRepository.getAppGuides()) {
                            titles.add(guideBook.getTitle());
                            Log.w("DataRepository", guideBook.getTitle());
                        }
                        subItems.addAll(titles);
                        break;
                    case "smartphone":
                        for (GuideBook guideBook : guideBookRepository.getSmartphoneGuides()) {
                            titles.add(guideBook.getTitle());
                            Log.w("DataRepository", guideBook.getTitle());
                        }
                        subItems.addAll(titles);
                        break;
                    case "guardian":
                        for (GuideBook guideBook : guideBookRepository.getControllerInstructions()) {
                            titles.add(guideBook.getTitle());
                            Log.w("DataRepository", guideBook.getTitle());
                        }
                        subItems.addAll(titles);
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